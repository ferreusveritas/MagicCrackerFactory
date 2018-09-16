package com.ferreusveritas.mcf.tileentity;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import com.ferreusveritas.mcf.util.CommandManager;
import com.ferreusveritas.mcf.util.MethodDescriptor;
import com.ferreusveritas.mcf.util.MethodDescriptor.MethodDescriptorProvider;
import com.ferreusveritas.mcf.util.MethodDescriptor.SyncProcess;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.SPacketChunkData;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkProviderServer;

public class TileTerraformer extends MCFPeripheral  {

	public TileTerraformer() {
		super("terraformer");
	}
	
	public enum ComputerMethod implements MethodDescriptorProvider {
		getBiome("nn", "xCoord, zCoord", 
			(world, peri, args) -> {
				int x = args.i();
				int z = args.i();
				Biome biome = world.getBiome(new BlockPos(x, 0, z));
				String biomeName = biome != null ? biome.getRegistryName().toString() : null;
				int biomeId =  biome != null ? Biome.getIdForBiome(biome) : 0;
				return new Object[] { biomeName, biomeId };
			}),
		
		setBiome("nnnnn", "xStart,zStart,xStop,zStop,biomeId",
			(world, peri, args) -> {
				int xStart = args.i();
				int zStart = args.i();
				int xStop = args.i();
				int zStop = args.i();
				int biomeId = args.i();
				
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
										return new Object[0];
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
				return new Object[0];
			}),
		
		getBiomeArray("nnnnn", "xStart,zStart,xEnd,yEnd,step", 
			(world, peri, args) -> {
				int xPosStart = args.i();
				int zPosStart = args.i();
				int xPosEnd = 	args.i();
				int zPosEnd = 	args.i();
				int step = 		args.i();
				step = MathHelper.clamp(step, 1, step);
				
				ArrayList<Biome> biomes = new ArrayList<Biome>();
				
				for(int z = zPosStart; z <= zPosEnd; z += step) {
					for(int x = xPosStart; x <= xPosEnd; x += step) {
						Biome biome = world.getBiomeProvider().getBiome(new BlockPos(x, 0, z));
						biomes.add(biome);
					}
				}
				
				Map<Integer, Integer> biomeIds = new HashMap<>();
				
				int i = 1;
				for(Biome biome: biomes) {//This waits for the request to be fulfilled
					biomeIds.put(i, Biome.getIdForBiome(biome));
					i++;
				}
					
				return new Object[] { biomeIds };
			}),
		
		getBiomeName("n", "biomeID", 
			(world, peri, args) -> {
				Biome biome = Biome.getBiomeForId(args.i());
				return new Object[] { biome == null ? null : biome.getRegistryName().toString() };
			}),
		
		getYTop("nn", "xCoord,zCoord", 
			(world, peri, args) -> {
				MutableBlockPos top = new MutableBlockPos(args.i(0), 0, args.i(1));
				Chunk chunk = world.getChunkFromBlockCoords(top);
				top.setY(chunk.getTopFilledSegment() + 16);
				
				while (top.getY() > 0) {
					IBlockState s = chunk.getBlockState(top);
					if (s.getMaterial() != Material.AIR) {
						return new Object[] { top.getY() };
					}
					top.setY(top.getY() - 1);
				}
				
				return new Object[] { 0 };
			}),
		
		getYTopArray("nnnnn", "xStart,zStart,xEnd,yEnd,step", 
				(world, peri, args) -> {
					int xPosStart = args.i();
					int zPosStart = args.i();
					int xPosEnd = 	args.i();
					int zPosEnd = 	args.i();
					int step = 		args.i();
					
					Map<Integer, Integer> heights = new HashMap<>();
					
					int i = 1;
					for(int z = zPosStart; z <= zPosEnd; z += step) {
						for(int x = xPosStart; x <= xPosEnd; x += step) {
							MutableBlockPos top = new MutableBlockPos(x, 0, z);
							Chunk chunk = world.getChunkFromBlockCoords(top);
							if(!chunk.isLoaded()) {
								return new Object[] { -1 }; 
							}
							
							top.setY(chunk.getTopFilledSegment() + 16);
							
							while (top.getY() > 0) {
								IBlockState s = chunk.getBlockState(top);
								if (s.getMaterial() != Material.AIR) {
									break;
								}
								top.setY(top.getY() - 1);
							}
							
							heights.put(i++, top.getY());
						}
					}
					
					return new Object[] { heights };
				}),
		
		getYTopSolid("nn", "xCoord,zCoord", 
			(world, peri, args) -> {
				int x = args.i();
				int z = args.i();
				
				MutableBlockPos top = new MutableBlockPos(x, 0, z);
				Chunk chunk = world.getChunkFromBlockCoords(top);
				top.setY(chunk.getTopFilledSegment() + 16);
				
				while (top.getY() > 0) {
					IBlockState s = chunk.getBlockState(top);
					if (s.getMaterial().blocksMovement()) {
						return new Object[] { top.getY() };
					}
					top.setY(top.getY() - 1);
				}
				
				return new Object[] { 0 };
			}),
		
		getYTopSolidArray("nnnnn", "xStart,zStart,xEnd,yEnd,step", 
			(world, peri, args) -> {
				int xPosStart = args.i();
				int zPosStart = args.i();
				int xPosEnd = 	args.i();
				int zPosEnd = 	args.i();
				int step = 		args.i();
				
				Map<Integer, Integer> heights = new HashMap<>();
				
				int i = 1;
				for(int z = zPosStart; z <= zPosEnd; z += step) {
					for(int x = xPosStart; x <= xPosEnd; x += step) {
						MutableBlockPos top = new MutableBlockPos(x, 0, z);
						Chunk chunk = world.getChunkFromBlockCoords(top);
						if(!chunk.isLoaded()) {
							return new Object[] { -1 }; 
						}
						
						top.setY(chunk.getTopFilledSegment() + 16);
						
						while (top.getY() > 0) {
							IBlockState s = chunk.getBlockState(top);
							if (s.getMaterial().blocksMovement()) {
								break;
							}
							top.setY(top.getY() - 1);
						}
						
						heights.put(i++, top.getY());
					}
				}
				
				return new Object[] { heights };
			}),
		
		getTemperature("nnn", "xCoord,yCoord,zCoord",
			(world, peri, args) -> {
				BlockPos pos = args.p();
				return new Object[] { world.getBiome(pos).getTemperature(pos) };
			}),
		
		generateChunk("nn", "xChunk,yChunk", 
			(world, peri, args) -> {
				int x = args.i();
				int z = args.i();
					
				IChunkProvider cp = world.getChunkProvider();
				if(cp instanceof ChunkProviderServer) {
					ChunkProviderServer cps = (ChunkProviderServer) cp;
					Chunk chunk = cps.chunkGenerator.generateChunk(x, z);
					long encChunkPos = ChunkPos.asLong(x, z);
					cps.id2ChunkMap.put(encChunkPos, chunk);
					chunk.onLoad();
					chunk.populate(cps, cps.chunkGenerator);
					chunk.markDirty();
					SPacketChunkData packet = new SPacketChunkData(chunk, 0xFFFF);
					for(EntityPlayer player : world.playerEntities) {
						if(player instanceof EntityPlayerMP) {
							((EntityPlayerMP)player).connection.sendPacket(packet);
						}
					}
				}
					
				return new Object[0];
			});
		
		public final MethodDescriptor md;
		private ComputerMethod(String argTypes, String args, SyncProcess process) { md = new MethodDescriptor(toString(), argTypes, args, process); }
		
		public static int getInt(Object[] args, int arg) {
			return ((Double)args[arg]).intValue();
		}

		@Override
		public MethodDescriptor getMethodDescriptor() {
			return md;
		}
	}
	
	static CommandManager<ComputerMethod> commandManager = new CommandManager<>(ComputerMethod.class);
	
	@Override
	public CommandManager getCommandManager() {
		return commandManager;
	}
	
}
