package com.ferreusveritas.mcf.tileentity;

import java.lang.reflect.Field;
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
import net.minecraft.world.biome.BiomeProvider;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkProviderServer;

public class TileTerraformer extends MCFPeripheral  {

	public static final String TERRAFORMER = "terraformer";

	public TileTerraformer() {
		super(TERRAFORMER);
	}
	
	public enum ComputerMethod implements MethodDescriptorProvider {
		getBiome("nn", "xCoord, zCoord", true, 
			(world, peri, args) -> {
				int x = args.i();
				int z = args.i();
				Biome biome = world.getBiome(new BlockPos(x, 0, z));
				String biomeName = biome != null ? biome.getRegistryName().toString() : null;
				int biomeId =  biome != null ? Biome.getIdForBiome(biome) : 0;
				return new Object[] { biomeName, biomeId };
			}),
		
		setBiome("nnnnn", "xStart,zStart,xStop,zStop,biomeId", false,
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
		
		getBiomeByteArray("nnnnn", "xPos,zPos,xLen,zLen,scale", true,
			(world, peri, args) -> {
				int xPos =  args.i();
				int zPos =  args.i();
				int xLen = 	args.i();
				int zLen = 	args.i();
				int scale = args.i();
				scale = MathHelper.clamp(scale, 1, scale);
				
				BiomeProvider biomeProvider = world.getBiomeProvider();
				byte[] biomeIds = new byte[xLen * zLen];
				int i = 0;//Java arrays start with 0
				
				if(scale == 1) { //Special efficiency case for scale 1
					Biome[] biomeArray = biomeProvider.getBiomes(null, xPos, zPos, xLen, zLen, false);
					for(Biome b : biomeArray) {
						biomeIds[i++] = (byte) Biome.getIdForBiome(b);
					}
				} else if(scale == 2 || scale == 4 || scale == 8) { //Special efficiency case for scale 2, 4 and 8
					//On 64 bit java this can temporarily eat some big memory since an object reference is 8 bytes..
					Biome[] biomeArray = biomeProvider.getBiomes(null, xPos, zPos, xLen * scale, zLen * scale, false);
					for(int z = 0; z < zLen * scale; z+=scale) {
						for(int x = 0; x < xLen * scale; x+=scale) {
							biomeIds[i++] = (byte) Biome.getIdForBiome(biomeArray[(z * xLen * scale) + x]);
						}
					}
				} else if( (scale == 16 && (zLen % 4) == 0) || (scale == 32 && (zLen % 16) == 0) ) { //Special efficiency case for scale 16 and 32 to not gobble ram
					int split = scale == 16 ? 4 : 16;
					Biome[] biomeArray = new Biome[xLen * scale * zLen * scale / split];
					for(int q = 0; q < split; q++) {
						biomeArray = biomeProvider.getBiomes(biomeArray, xPos, zPos + (q * zLen * scale / split), xLen * scale, zLen * scale / split, false);
						for(int z = 0; z < zLen * scale / split; z+=scale) {
							for(int x = 0; x < xLen * scale; x+=scale) {
								biomeIds[i++] = (byte) Biome.getIdForBiome(biomeArray[(z * xLen * scale) + x]);
							}
						}
					}
				} else { //Diminishing returns on the above strategy after this point.  Just sample each biome by individual block
					MutableBlockPos blockPos = new MutableBlockPos();
					Biome[] singleBiome = new Biome[1];
					for(int z = 0; z < zLen; z++) {
						for(int x = 0; x < xLen; x++) {
							blockPos.setPos(xPos + (x * scale), 0, zPos + (z * scale));
							singleBiome = biomeProvider.getBiomes(singleBiome, blockPos.getX(), blockPos.getZ(), 1, 1, false);
							biomeIds[i++] = (byte) Biome.getIdForBiome(singleBiome[0]);
						}
					}
				}
				
				return new Object[] { biomeIds };
			}),
		
		getBiomeName("n", "biomeID", true,
			(world, peri, args) -> {
				Biome biome = Biome.getBiomeForId(args.i());
				return new Object[] { biome == null ? null : biome.getRegistryName().toString() };
			}),
		
		getYTop("nn", "xCoord,zCoord,solid", true, 
			(world, peri, args) -> {
				MutableBlockPos top = new MutableBlockPos(args.i(0), 0, args.i(1));
				boolean solid = args.b(2);
				Chunk chunk = world.getChunkFromBlockCoords(top);
				top.setY(chunk.getTopFilledSegment() + 16);
				
				while (top.getY() > 0) {
					IBlockState s = chunk.getBlockState(top);
					if (solid ? s.getMaterial().blocksMovement() : (s.getMaterial() != Material.AIR)) {
						break;
					}
					top.setY(top.getY() - 1);
				}
				
				return new Object[] { 0 };
			}),
		
		getYTopArray("nnnnn", "xPos,zPos,xLen,zLen,scale,solid", true, 
			(world, peri, args) -> {
				int xPos =  args.i();
				int zPos =  args.i();
				int xLen = 	args.i();
				int zLen = 	args.i();
				int scale = args.i();
				boolean solid = args.b();
				scale = MathHelper.clamp(scale, 1, scale);
				
				Map<Integer, Integer> heights = new HashMap<>();
				
				MutableBlockPos top = new MutableBlockPos();
				int i = 1;
				for(int z = 0; z < zLen; z++) {
					for(int x = 0; x < xLen; x++) {
						top.setPos(xPos + (x * scale), 0, zPos + (z * scale));
						Chunk chunk = world.getChunkFromBlockCoords(top);
						if(!chunk.isLoaded()) {
							return new Object[] { -1 }; 
						}
						
						top.setY(chunk.getTopFilledSegment() + 16);
						
						while (top.getY() > 0) {
							IBlockState s = chunk.getBlockState(top);
							if (solid ? s.getMaterial().blocksMovement() : (s.getMaterial() != Material.AIR)) {
								break;
							}
							top.setY(top.getY() - 1);
						}
						
						heights.put(i++, top.getY());
					}
				}
				
				return new Object[] { heights };
			}),
		
		getTemperature("nnn", "xCoord,yCoord,zCoord", true,
			(world, peri, args) -> {
				BlockPos pos = args.p();
				return new Object[] { world.getBiome(pos).getTemperature(pos) };
			}),
		
		generateChunk("nn", "xChunk,yChunk", false,
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
		private ComputerMethod(String argTypes, String args, boolean synced, SyncProcess process) { md = new MethodDescriptor(toString(), argTypes, args, process, synced); }
		
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
