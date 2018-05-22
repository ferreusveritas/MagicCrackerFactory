package com.ferreusveritas.mcf.tileentity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.ferreusveritas.mcf.blocks.BlockCartographer;

import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.SPacketMaps;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.storage.MapData;

public class TileCartographer extends TileEntity implements IPeripheral, ITickable  {

	int mapNum = 0;
	MapData currMapData;
	
	public MapData setCurrMapData(int mapNum) {
		this.mapNum = mapNum;
		this.currMapData = (MapData) world.loadData(MapData.class, "map_" + mapNum);
		return this.currMapData;
	}
	
	public MapData getCurrMapData() {
		return currMapData != null ? currMapData : setCurrMapData(0);
	}
	
	public void setMapPixel(int x, int z, int color52, int index4) {
		setMapPixel(x, z, color52 * 4 | index4);
	}
	
	public void setMapPixel(int x, int z, int colorFull) {
		if(x >= 0 && x < 128 && z >= 0 && z < 128) {
			getCurrMapData().colors[x + z * 128] = (byte) (colorFull >= 0 && colorFull <= (51 * 4) ? colorFull : 0);
		}
	}

	public void updateMap() {
		MapData mapData = getCurrMapData();
		mapData.markDirty();//Mark as dirty so the changes save to disk
		Packet<?> packet = new SPacketMaps(mapNum, mapData.scale, mapData.trackingPosition, mapData.mapDecorations.values(), mapData.colors, 0, 0, 128, 128);

		for(EntityPlayer player : world.playerEntities) {
			if(player instanceof EntityPlayerMP) {
				((EntityPlayerMP)player).connection.sendPacket(packet);
			}
		}
	}
	
	public enum ComputerMethod {
		setMapNum("n", true, "mapNum"),
		setPixel("nn", true, "x", "z"),
		updateMap("", true),
		getBiome("nn", false, "xCoord", "zCoord");
		
		private final String argTypes;
		private final String args[];
		private final boolean cached;

		private ComputerMethod(String argTypes, boolean cached, String ... args) {
			this.argTypes = argTypes;
			this.args = args;
			this.cached = cached;
		}
		
		public boolean isCached() {
			return cached;
		}
		
		public boolean isValidArguments(Object[] arguments) {
			if(arguments.length >= argTypes.length()) {
				for (int i = 0; i < argTypes.length(); i++){
					if(!CCDataType.byIdent(argTypes.charAt(i)).isInstance(arguments[i])) {
						return false;
					}
				}
				return true;
			}
			return false;
		}
		
		public boolean validateArguments(Object[] arguments) throws LuaException {
			if(isValidArguments(arguments)) {
				return true;
			}
			throw new LuaException(invalidArgumentsError());
		}
		
		public String invalidArgumentsError() {
			String error = "Expected: " + this.toString();
			for (int i = 0; i < argTypes.length(); i++){
				error += " " + args[i] + "<" + CCDataType.byIdent(argTypes.charAt(i)).name + ">";
			}
			return error;
		}
	}
	
	private class CachedCommand {
		ComputerMethod method;
		Object[] arguments;
		int argRead = 0;
		
		public CachedCommand(int method, Object[] args) {
			this.method = ComputerMethod.values()[method];
			this.arguments = args;
		}
		
		/*public double d() {
			return ((Double)arguments[argRead++]).doubleValue();
		}*/
		
		public int i() {
			return ((Double)arguments[argRead++]).intValue();
		}
		
		/*public String s() {
			return ((String)arguments[argRead++]);
		}*/
		
		/*public boolean b() {
			return ((Boolean)arguments[argRead++]).booleanValue();
		}*/
	}
	
	private ArrayList<CachedCommand> cachedCommands = new ArrayList<CachedCommand>(1);

	//Dealing with multithreaded biome requests
	BiomeRequest biomeRequest = null;
	
	public static final int numMethods = ComputerMethod.values().length;
	public static final String[] methodNames = new String[numMethods]; 
	static {
		for(ComputerMethod method : ComputerMethod.values()) { 
			methodNames[method.ordinal()] = method.toString(); 
		}
	}
	
	public void cacheCommand(int method, Object[] args) {
		synchronized (cachedCommands) {
			cachedCommands.add(new CachedCommand(method, args));
		}
	}
	
	@Override
	public void update() {
		
		BlockCartographer cartographer = (BlockCartographer)getBlockType();
		World world = getWorld();
		
		//Run commands that are cached that shouldn't be in the lua thread
		synchronized(cachedCommands) {
			if(cachedCommands.size() > 0) { 
				if(cartographer != null) {
					for(CachedCommand cmd:  cachedCommands) {
						switch(cmd.method) {
							case setMapNum: setCurrMapData(cmd.i()); break;
							case setPixel: setMapPixel(cmd.i(), cmd.i(), cmd.i()); break;
							case updateMap: updateMap(); break;
							default: break;
						}
					}
					cachedCommands.clear();
				}
			}
		}
		
		//Fulfill data requests
		if(biomeRequest != null) {
			biomeRequest.process(world);
		}
	}
	
	@Override
	public String getType() {
		return "cartographer";
	}
	
	@Override
	public String[] getMethodNames() {
		return methodNames;
	}
	
	/**
	* I hear ya Dan!  Make the function threadsafe by caching the commmands to run in the main world server thread and not the lua thread.
	*/
	@Override
	public Object[] callMethod(IComputerAccess computer, ILuaContext context, int methodNum, Object[] arguments) throws LuaException {
		if(methodNum < 0 || methodNum >= numMethods) {
			throw new IllegalArgumentException("Invalid method number");
		}
		
		BlockCartographer dendroCoil = (BlockCartographer)getBlockType();
		World world = getWorld();
		
		if(!world.isRemote && dendroCoil != null) {
			ComputerMethod method = ComputerMethod.values()[methodNum];

			if(method.validateArguments(arguments)) {
				switch(method) {
					case getBiome:
						if( (arguments[0] instanceof Double) &&
							(arguments[1] instanceof Double) &&
							(arguments[2] instanceof Double) &&
							(arguments[3] instanceof Double) &&
							(arguments[4] instanceof Double) ) {
							int xPosStart = ((Double)arguments[0]).intValue();
							int zPosStart = ((Double)arguments[1]).intValue();
							int xPosEnd = ((Double)arguments[2]).intValue();
							int zPosEnd = ((Double)arguments[3]).intValue();
							int step = ((Double)arguments[4]).intValue();
							
							biomeRequest = new BiomeRequest(
								new BlockPos(xPosStart, 0, zPosStart),
								new BlockPos(xPosEnd, 0, zPosEnd),
								step);
					
							Map<Integer, String> biomeNames = new HashMap<>();
							Map<Integer, Integer> biomeIds = new HashMap<>();
							
							int i = 1;
							for(Biome biome: biomeRequest.getBiomes()) {
								biomeNames.put(i, biome.getBiomeName());
								biomeIds.put(i, Biome.getIdForBiome(biome));
								i++;
							}
							
							biomeRequest = null;
							
							return new Object[] { biomeNames, biomeIds };
						}
						return new Object[] { new Object[] {}, new Object[] {} };
					default:
						if(method.isCached()) {
							cacheCommand(methodNum, arguments);
						}
				}
			}
		}
		
		return null;
	}
	
	private class BiomeRequest {
		public BlockPos startPos;
		public BlockPos endPos;
		public int step;
		public boolean fulfilled = false;
		public ArrayList<Biome> result = new ArrayList<Biome>();

		public BiomeRequest(BlockPos start, BlockPos end, int step) {
			this.startPos = start;
			this.endPos = end;
			this.step = step;
		}
		
		//This is run by the server thread
		public synchronized void process(World world) {
			if(!fulfilled) {
				for(int z = startPos.getZ(); z < endPos.getZ(); z += step) {
					for(int x = startPos.getX(); x < endPos.getX(); x += step) {
						Biome biome = world.getBiomeProvider().getBiome(new BlockPos(x, 0, z));
						result.add(biome);
					}
				}
				fulfilled = true;
				notifyAll();
			}
		}

		//This is run by the CC thread
		public synchronized ArrayList<Biome> getBiomes() {
			while(!fulfilled) {
				try {
					wait();
				} catch (InterruptedException e) {}
			}
			return result;
		}

	}
	
	@Override
	public boolean equals(IPeripheral other) {
		return this == other;
	}

}
