package com.ferreusveritas.mcf.tileentity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.ferreusveritas.mcf.blocks.BlockCartographer;

import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.SPacketMaps;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
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
	
	public int getMapPixel(int x, int z) {
		if(x >= 0 && x < 128 && z >= 0 && z < 128) {
			return getCurrMapData().colors[x + z * 128];
		}
		return 0;
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
		getMapNum("n", true, "mapNum"),
		setMapPixel("nn", true, "x", "z"),
		getMapPixel("nn", false, "x", "z"),
		setMapCenter("nn", true, "x", "z"),
		getMapCenter("", false),
		setMapScale("n", true, "scale"),
		getMapScale("", false),
		setMapDimension("n", true, "dimension"),
		getMapDimension("", false),
		updateMap("", true),
		getBiome("nn", false, "xCoord", "zCoord"),
		getYTop("nn", false, "xCoord", "zCoord"),
		getYTopSolid("nn", false, "xCoord", "zCoord"),
		getTemperature("nnn", false, "xCoord", "yCoord", "zCoord");
		
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
							case setMapPixel: setMapPixel(cmd.i(), cmd.i(), cmd.i()); break;
							case setMapCenter: getCurrMapData().xCenter = cmd.i(); getCurrMapData().zCenter = cmd.i(); break;
							case setMapScale: getCurrMapData().scale = (byte) MathHelper.clamp(cmd.i(), 0, 4);
							case setMapDimension: getCurrMapData().dimension = cmd.i(); break;
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
	
	private int getInt(Object[] arguments, int arg) {
		return ((Double)arguments[arg]).intValue();
	}
	
	/**
	* I hear ya Dan!  Make the function threadsafe by caching the commmands to run in the main world server thread and not the lua thread.
	*/
	@Override
	public Object[] callMethod(IComputerAccess computer, ILuaContext context, int methodNum, Object[] arguments) throws LuaException {
		if(methodNum < 0 || methodNum >= numMethods) {
			throw new IllegalArgumentException("Invalid method number");
		}
		
		BlockCartographer cartographer = (BlockCartographer)getBlockType();
		World world = getWorld();
		
		if(!world.isRemote && cartographer != null) {
			ComputerMethod method = ComputerMethod.values()[methodNum];

			if(method.validateArguments(arguments)) {
				switch(method) {
					case getMapNum:
						return new Object[] { mapNum };
					case getMapPixel:
						return new Object[] { getMapPixel(getInt(arguments, 0), getInt(arguments, 1)) };
					case getMapCenter:
						return new Object[] { getCurrMapData().xCenter, getCurrMapData().zCenter };
					case getMapScale:
						return new Object[] { getCurrMapData().scale };
					case getMapDimension:
						return new Object[] { getCurrMapData().dimension }; 
					case getBiome:
						int xPosStart = getInt(arguments, 0);
						int zPosStart = getInt(arguments, 1);
						int xPosEnd = 	getInt(arguments, 2);
						int zPosEnd = 	getInt(arguments, 3);
						int step = 		getInt(arguments, 4);
							
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
					case getYTop:
						return new Object[] { getYTop(getInt(arguments, 0), getInt(arguments, 0)) };
					case getYTopSolid:
						return new Object[] { getYTopSolid(getInt(arguments, 0), getInt(arguments, 0)) };
					case getTemperature:
						BlockPos pos = new BlockPos(getInt(arguments, 0), getInt(arguments, 1), getInt(arguments, 2));
				        Biome biome = world.getBiome(pos);
				        float temp = biome.getTemperature(pos);
						return new Object[] { temp };
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
	
	public int getYTop(int x, int z) {
		MutableBlockPos top = new MutableBlockPos(pos.getX() + x - 64, 0, pos.getZ() + z - 64);
		Chunk chunk = world.getChunkFromBlockCoords(top);
		top.setY(chunk.getTopFilledSegment() + 16);

		while (top.getY() > 0) {
			IBlockState s = chunk.getBlockState(top);
			if (s.getMaterial() != Material.AIR) {
				return top.getY();
			}
			top.setY(top.getY() - 1);
		}

		return 0;
	}

	public int getYTopSolid(int x, int z) {
		MutableBlockPos top = new MutableBlockPos(pos.getX() + x - 64, 0, pos.getZ() + z - 64);
		Chunk chunk = world.getChunkFromBlockCoords(top);
		top.setY(chunk.getTopFilledSegment() + 16);

		while (top.getY() > 0) {
			IBlockState s = chunk.getBlockState(top);
			if (s.getMaterial().blocksMovement()) {
				return top.getY();
			}
			top.setY(top.getY() - 1);
		}

		return 0;
	}

	
	@Override
	public boolean equals(IPeripheral other) {
		return this == other;
	}

}
