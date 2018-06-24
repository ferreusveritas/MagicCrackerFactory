package com.ferreusveritas.mcf.tileentity;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import com.ferreusveritas.mcf.blocks.BlockPeripheral;
import com.ferreusveritas.mcf.util.CommandManager;
import com.ferreusveritas.mcf.util.MethodDescriptor;
import com.ferreusveritas.mcf.util.Util;

import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.SPacketChunkData;
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

public class TileTerraformer extends TileEntity implements IPeripheral, ITickable  {
	
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
		getBiome("nn", false, "xCoord", "zCoord"),
		setBiome("nnnnn", true,  "xStart", "zStart", "xStop", "zStop", "biomeId"),
		getBiomeArray("nnnnn", false, "xStart", "zStart", "xEnd", "yEnd", "step"),
		getBiomeName("n", false, "BiomeID"),
		getYTop("nn", false, "xCoord", "zCoord"),
		getYTopSolid("nn", false, "xCoord", "zCoord"),
		getTemperature("nnn", false, "xCoord", "yCoord", "zCoord");
		
		final MethodDescriptor md;
		private ComputerMethod(String argTypes, boolean cached, String ... args) { md = new MethodDescriptor(argTypes, cached, args); }
	}
	
	static CommandManager<ComputerMethod> commandManager = new CommandManager<>(ComputerMethod.class);

	//Dealing with multithreaded biome requests
	BiomeRequest biomeRequest = null;
	
	@Override
	public void update() {
		
		BlockPeripheral cartographer = (BlockPeripheral)getBlockType();
		World world = getWorld();
		
		//Run commands that are cached that shouldn't be in the lua thread
		synchronized(commandManager) {
			if(cartographer != null) {
				for(CommandManager<ComputerMethod>.CachedCommand cmd:  commandManager.getCachedCommands()) {
					switch(cmd.method) {
					case setBiome: setBiome(cmd.i(), cmd.i(), cmd.i(), cmd.i(), cmd.i()); break;
					default: break;
					}
				}
				commandManager.clear();
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
		return commandManager.getMethodNames();
	}
	
	private int getInt(Object[] arguments, int arg) {
		return ((Double)arguments[arg]).intValue();
	}
	
	/**
	* I hear ya Dan!  Make the function threadsafe by caching the commmands to run in the main world server thread and not the lua thread.
	*/
	@Override
	public Object[] callMethod(IComputerAccess computer, ILuaContext context, int methodNum, Object[] arguments) throws LuaException {
		if(methodNum < 0 || methodNum >= commandManager.getNumMethods()) {
			throw new IllegalArgumentException("Invalid method number");
		}
		
		BlockPeripheral cartographer = (BlockPeripheral)getBlockType();
		World world = getWorld();
		
		if(!world.isRemote && cartographer != null) {
			ComputerMethod method = ComputerMethod.values()[methodNum];
			
			if(method.md.validateArguments(arguments)) {
				switch(method) {
					case getBiome: {
						Biome biome = world.getBiome(new BlockPos(getInt(arguments, 0), 0, getInt(arguments, 1)));
						return new Object[] { getBiomeName(biome), Biome.getIdForBiome(biome) };
						}
					case getBiomeArray:
						int xPosStart = getInt(arguments, 0);
						int zPosStart = getInt(arguments, 1);
						int xPosEnd = 	getInt(arguments, 2);
						int zPosEnd = 	getInt(arguments, 3);
						int step = 		getInt(arguments, 4);
							
						biomeRequest = new BiomeRequest(
							new BlockPos(xPosStart, 0, zPosStart),
							new BlockPos(xPosEnd, 0, zPosEnd),
							step);
						
						Map<Integer, Integer> biomeIds = new HashMap<>();
						
						int i = 1;
						for(Biome biome: biomeRequest.getBiomes()) {//This waits for the request to be fulfilled
							biomeIds.put(i, Biome.getIdForBiome(biome));
							i++;
						}
						
						biomeRequest = null;
							
						return new Object[] { biomeIds };
					case getBiomeName: {
						Biome biome = Biome.getBiomeForId(getInt(arguments, 0));
						return new Object[] { biome == null ? null : getBiomeName(biome) };
						}
					case getYTop:
						return new Object[] { getYTop(getInt(arguments, 0), getInt(arguments, 1)) };
					case getYTopSolid:
						return new Object[] { getYTopSolid(getInt(arguments, 0), getInt(arguments, 1)) };
					case getTemperature:{
						BlockPos pos = new BlockPos(getInt(arguments, 0), getInt(arguments, 1), getInt(arguments, 2));
						Biome biome = world.getBiome(pos);
						float temp = biome.getTemperature(pos);
						return new Object[] { temp };
						}
					default:
						if(method.md.isCached()) {
							synchronized (commandManager) {
								commandManager.cacheCommand(methodNum, arguments);
							}
						}
				}
			}
		}
		
		return null;
	}
	
	//Biome#getBiomeName is sodding client side only.
	public static String getBiomeName(Biome biome) {
		return (String) Util.getRestrictedObject(Biome.class, biome, "field_185412_a", "biomeName");
	}
	
	private void setBiome(int xStart, int zStart, int xStop, int zStop, int biomeId) {
		
		if(Biome.REGISTRY.getObjectById(biomeId) != null) { //Verify the biomeId is tied to a valid Biome
			
			//Open up the blockBiomeArray field in the Chunk class
			Field blockBiomeArrayField = null;
			for(String field : new String[] {"field_76651_r", "blockBiomeArray"}) {//Obfuscated and Deobfuscated field names
				try {
					blockBiomeArrayField = Chunk.class.getDeclaredField(field);
					break;//If we get this far then no exception occurred and we can break out of the search loop
				} catch (NoSuchFieldException | SecurityException e) { }
			}
			
			if(blockBiomeArrayField != null) {
				blockBiomeArrayField.setAccessible(true);
				
				byte[] blockBiomeArray;
				
				HashSet<Chunk> chunksToUpdate = new HashSet<>();
				
				for(int z = zStart; z <= zStop; z++) {
					for(int x = xStart; x <= xStop; x++) {
						Chunk chunk = world.getChunkFromBlockCoords(new BlockPos(x, 0, z));
						if(chunk != null) {
							try {
								blockBiomeArray = (byte[]) blockBiomeArrayField.get(chunk);
							} catch (IllegalArgumentException | IllegalAccessException e) {
								e.printStackTrace();
								return;
							}
							blockBiomeArray[((z & 0xF) * 16) + (x & 0xF)] = (byte) MathHelper.clamp(biomeId, 0, 255);//Squirt in the new biome id
							chunk.markDirty();
							chunksToUpdate.add(chunk);//Add to the list of chunks to send to the player clients
						}
					}
				}
				
				//Update all of the clients with new chunk data
				for(Chunk chunk: chunksToUpdate) {
					SPacketChunkData packet = new SPacketChunkData(chunk, 0xFFFF);//We send the whole chunk since it's the only way to send the biome data
					for(EntityPlayer player : world.playerEntities) {
						if(player instanceof EntityPlayerMP) {
							((EntityPlayerMP)player).connection.sendPacket(packet);
						}
					}
				}
			}
		}
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
	
	private int getYTop(int x, int z) {
		MutableBlockPos top = new MutableBlockPos(x, 0, z);
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
	
	private int getYTopSolid(int x, int z) {
		MutableBlockPos top = new MutableBlockPos(x, 0, z);
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
