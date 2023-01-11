package com.ferreusveritas.mcf.peripheral;

import com.ferreusveritas.mcf.tileentity.TerraformerTileEntity;
import com.ferreusveritas.mcf.util.biome.BiomeSetter;
import com.ferreusveritas.mcf.util.biome.DefaultMagnifierBiomeSetter;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.play.server.SChunkDataPacket;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.ResourceLocationException;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.MutableRegistry;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkLightProvider;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.server.ServerChunkProvider;

import javax.annotation.Nullable;
import java.util.*;

public class TerraformerPeripheral extends MCFPeripheral<TerraformerTileEntity> {

    public TerraformerPeripheral(TerraformerTileEntity block) {
        super(block);
    }

    @LuaFunction
    public String getBiome(int x, int z) {
        Biome biome = block.getLevel().getBiome(new BlockPos(x, 0, z));
        return String.valueOf(biome == null ? null : biome.getRegistryName());
    }

    @LuaFunction
    public String[] getBiomeNames() {
        return getBiomeRegistry().stream().map(Biome::getRegistryName).map(ResourceLocation::toString).toArray(String[]::new);
    }

    @LuaFunction
    public void setBiome(int xStart, int zStart, int xEnd, int zEnd, String biomeName) throws LuaException {
        setBiome3D(xStart, 0, zStart, xEnd, 0, zEnd, biomeName);
    }

    @LuaFunction
    public void setBiome3D(int xStart, int yStart, int zStart, int xEnd, int yEnd, int zEnd, String biomeName) throws LuaException {
        try {
            Biome biome = getBiome(new ResourceLocation(biomeName));
            if (biome != null) {
                Set<Chunk> chunksToUpdate = setBiomeInChunks(xStart, yStart, zStart, xEnd, yEnd, zEnd, biome);
                chunksToUpdate.forEach(Chunk::markUnsaved);
                sendChunkUpdatesToClients(chunksToUpdate);
            }
        } catch (ResourceLocationException e) {
            throw new LuaException("Biome name given to setBiome invalid: " + e.getMessage());
        }
    }

    private Biome getBiome(ResourceLocation biomeName) {
        return getBiomeRegistry().get(biomeName);
    }

    private MutableRegistry<Biome> getBiomeRegistry() {
        return block.getLevel().getServer().registryAccess().registryOrThrow(Registry.BIOME_REGISTRY);
    }

    /**
     * Due to the way {@link net.minecraft.world.biome.IBiomeMagnifier}s work, this is not strict to the given range. It is, however, set up so that
     * all blocks within the given range will be the given biome, but there will be some overlap in surrounding blocks.
     */
    private Set<Chunk> setBiomeInChunks(int xStart, int yStart, int zStart, int xEnd, int yEnd, int zEnd, Biome biome) {
        BiomeSetter biomeSetter = BiomeSetter.SETTERS.getOrDefault(block.getLevel().dimensionType().getBiomeZoomer(), DefaultMagnifierBiomeSetter.INSTANCE);
        HashSet<Chunk> chunksToUpdate = new HashSet<>();
        for (int z = zStart; z <= zEnd; z++) {
            for (int y = yStart; y <= yEnd; y++) {
                for (int x = xStart; x <= xEnd; x++) {
                    chunksToUpdate.add(biomeSetter.setBiome(block.getLevel().getBiomeManager().biomeZoomSeed, x, y, z, block.getLevel(), biome));
                }
            }
        }
        return chunksToUpdate;
    }

    private void sendChunkUpdatesToClients(Collection<Chunk> chunksToUpdate) {
        for (Chunk chunk : chunksToUpdate) {
            SChunkDataPacket packet = new SChunkDataPacket(chunk, 0xFFFF);//We send the whole chunk since it's the only way to send the biome data
            block.getLevel().players().stream()
                    .filter(player -> player instanceof ServerPlayerEntity)
                    .forEach(player -> ((ServerPlayerEntity) player).connection.send(packet));
        }
    }

    @LuaFunction
    public String[] getBiomeArray(int xPos, int zPos, int xLen, int zLen, int scale) {
        scale = Math.max(1, scale);

        String[] biomeNames = new String[xLen * zLen];
        int i = 0;//Java arrays start with 0

        if (scale == 1) { //Special efficiency case for scale 1
            Biome[] biomeArray = getBiomes(null, xPos, zPos, xLen, zLen);
            for (Biome b : biomeArray) {
                biomeNames[i++] = String.valueOf(b.getRegistryName()).intern();
            }
        } else if (scale == 2 || scale == 4 || scale == 8) { //Special efficiency case for scale 2, 4 and 8
            //On 64 bit java this can temporarily eat some big memory since an object reference is 8 bytes..
            Biome[] biomeArray = getBiomes(null, xPos, zPos, xLen * scale, zLen * scale);
            for (int z = 0; z < zLen * scale; z += scale) {
                for (int x = 0; x < xLen * scale; x += scale) {
                    biomeNames[i++] = String.valueOf(biomeArray[(z * xLen * scale) + x].getRegistryName()).intern();
                }
            }
        } else if ((scale == 16 && (zLen % 4) == 0) || (scale == 32 && (zLen % 16) == 0)) { //Special efficiency case for scale 16 and 32 to not gobble ram
            int split = scale == 16 ? 4 : 16;
            Biome[] biomeArray = new Biome[xLen * scale * zLen * scale / split];
            for (int q = 0; q < split; q++) {
                biomeArray = getBiomes(biomeArray, xPos, zPos + (q * zLen * scale / split), xLen * scale, zLen * scale / split);
                for (int z = 0; z < zLen * scale / split; z += scale) {
                    for (int x = 0; x < xLen * scale; x += scale) {
                        biomeNames[i++] = String.valueOf(biomeArray[(z * xLen * scale) + x].getRegistryName()).intern();
                    }
                }
            }
        } else { //Diminishing returns on the above strategy after this point.  Just sample each biome by individual block
            BlockPos.Mutable blockPos = new BlockPos.Mutable();
            Biome[] singleBiome = new Biome[1];
            for (int z = 0; z < zLen; z++) {
                for (int x = 0; x < xLen; x++) {
                    blockPos.set(xPos + (x * scale), 0, zPos + (z * scale));
                    singleBiome = getBiomes(singleBiome, blockPos.getX(), blockPos.getZ(), 1, 1);
                    biomeNames[i++] = String.valueOf(singleBiome[0].getRegistryName()).intern();
                }
            }
        }

        return biomeNames;
    }

    private Biome[] getBiomes(@Nullable Biome[] biomes, int startX, int startZ, int xLen, int zLen) {
        int size = xLen * zLen;
        if (biomes == null) {
            biomes = new Biome[size];
        }
        for (int x = 0; x < xLen; ++x) {
            for (int z = 0; z < zLen; ++z) {
                biomes[z * xLen + x] = block.getLevel().getBiome(new BlockPos(startX + x, 0, startZ + z));
            }
        }
        return biomes;
    }

    @LuaFunction
    public int getYTop(int x, int z, boolean solid) {
        return block.getLevel().getHeight(solid ? Heightmap.Type.MOTION_BLOCKING : Heightmap.Type.WORLD_SURFACE, x, z);
    }

    @LuaFunction
    public Object getYTopArray(int xPos, int zPos, int xLen, int zLen, int scale, boolean solid) {
        scale = MathHelper.clamp(scale, 1, scale);

        Map<Integer, Integer> heights = new HashMap<>();

        BlockPos.Mutable pos = new BlockPos.Mutable();
        int i = 1;
        for (int z = 0; z < zLen; z++) {
            for (int x = 0; x < xLen; x++) {
                pos.set(xPos + (x * scale), 0, zPos + (z * scale));
                if (!block.getLevel().isLoaded(pos)) {
                    return -1;
                }
                heights.put(i++, getYTop(pos.getX(), pos.getZ(), solid));
            }
        }

        return heights;
    }

    @LuaFunction
    public float getTemperature(int x, int y, int z) {
        BlockPos pos = new BlockPos(x, y, z);
        return block.getLevel().getBiome(pos).getTemperature(pos);
    }

    @LuaFunction
    public void generateChunk(int x, int z) {
        IChunkLightProvider chunkSource = block.getLevel().getChunkSource();
        if (chunkSource instanceof ServerChunkProvider) {
            ServerChunkProvider chunkProvider = (ServerChunkProvider) chunkSource;
//            Chunk chunk = chunkProvider.generator.generate(x, z);
//            long encChunkPos = ChunkPos.asLong(x, z);
//            chunkProvider.id2ChunkMap.put(encChunkPos, chunk);
//            chunk.onLoad();
//            chunk.populate(chunkProvider, chunkProvider.chunkGenerator);
//            chunk.markDirty();
//            SPacketChunkData packet = new SPacketChunkData(chunk, 0xFFFF);
//            for (EntityPlayer player : world.playerEntities) {
//                if (player instanceof EntityPlayerMP) {
//                    ((EntityPlayerMP) player).connection.sendPacket(packet);
//                }
//            }
        }
    }

}
