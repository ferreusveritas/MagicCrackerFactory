package com.ferreusveritas.mcf.peripheral;

import com.ferreusveritas.mcf.block.entity.TerraformerBlockEntity;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import net.minecraft.ResourceLocationException;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.SectionPos;
import net.minecraft.network.protocol.game.ClientboundLevelChunkWithLightPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.levelgen.Heightmap;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.Map.Entry;

public class TerraformerPeripheral extends MCFPeripheral<TerraformerBlockEntity> {

    private final Map<Holder<Biome>, Byte> byteMap = new HashMap<>();

    public TerraformerPeripheral(TerraformerBlockEntity block) {
        super(block);
    }

    @LuaFunction
    public final String getBiome(int x, int y, int z) {
        Holder<Biome> biome = block.getLevel().getBiome(new BlockPos(x, y, z));
        return biome.unwrapKey().map(ResourceKey::location).map(String::valueOf).orElse(null);
    }

    @LuaFunction
    public final String[] getBiomeNames() {
        return getBiomeRegistry().stream().map(Biome::getRegistryName).map(ResourceLocation::toString).toArray(String[]::new);
    }

    @LuaFunction
    public final void setBiome(int xStart, int zStart, int xEnd, int zEnd, String biomeName) throws LuaException {
        setBiome3D(xStart, block.getLevel().getMinBuildHeight(), zStart, xEnd, block.getLevel().getMaxBuildHeight(), zEnd, biomeName);
    }

    @LuaFunction
    public final void setBiome3D(int xStart, int yStart, int zStart, int xEnd, int yEnd, int zEnd, String biomeName) throws LuaException {
        Holder<Biome> biome = getBiomeOrThrow(biomeName);
        Set<LevelChunk> chunksToUpdate = setBiomeInChunks(xStart, yStart, zStart, xEnd, yEnd, zEnd, biome);
        chunksToUpdate.forEach(c -> c.setUnsaved(true));
        sendChunkUpdatesToClients(chunksToUpdate);
    }

    private Holder<Biome> getBiomeOrThrow(String biomeName) throws LuaException {
        try {
            return getBiome(new ResourceLocation(biomeName)).orElseThrow(() -> new LuaException("Could not find biome with name: " + biomeName));
        } catch (ResourceLocationException e) {
            throw new LuaException("Invalid biome name: " + biomeName);
        }
    }

    private Optional<Holder<Biome>> getBiome(ResourceLocation biomeName) {
        return getBiomeRegistry().getHolder(ResourceKey.create(Registry.BIOME_REGISTRY, biomeName));
    }

    private Registry<Biome> getBiomeRegistry() {
        return block.getLevel().getServer().registryAccess().registryOrThrow(Registry.BIOME_REGISTRY);
    }

    private Set<LevelChunk> setBiomeInChunks(int xStart, int yStart, int zStart, int xEnd, int yEnd, int zEnd, Holder<Biome> biome) {
        HashSet<LevelChunk> chunksToUpdate = new HashSet<>();
        for (int z = zStart; z <= zEnd; z++) {
            for (int y = yStart; y <= yEnd; y++) {
                for (int x = xStart; x <= xEnd; x++) {
                    chunksToUpdate.add(setBiomeAtPos(x, y, z, biome));
                }
            }
        }
        return chunksToUpdate;
    }

    /**
     * @return the chunk at the given coordinates
     */
    private LevelChunk setBiomeAtPos(int x, int y, int z, Holder<Biome> biome) {
        Level level = block.getLevel();
        LevelChunk chunk = level.getChunk(SectionPos.blockToSectionCoord(x), SectionPos.blockToSectionCoord(z));
        int sectionIndex = chunk.getSectionIndex(y);
        if (sectionIndex >= level.getMinSection() && sectionIndex <= level.getMaxSection()) {
            LevelChunkSection chunkSection = chunk.getSection(sectionIndex);
            chunkSection.getBiomes().set(x & 3, y & 3, z & 3, biome);
        }
        return chunk;
    }

    private void sendChunkUpdatesToClients(Iterable<LevelChunk> chunksToUpdate) {
        for (LevelChunk chunk : chunksToUpdate) {
            // We send the whole chunk since it's the only way to send the biome data
            ClientboundLevelChunkWithLightPacket packet = new ClientboundLevelChunkWithLightPacket(chunk, chunk.getLevel().getLightEngine(), null, null, true);
            block.getLevel().players().stream()
                    .filter(player -> player instanceof ServerPlayer)
                    .forEach(player -> ((ServerPlayer) player).connection.send(packet));
        }
    }

    @LuaFunction
    public final String[] getBiomeArray(int xPos, int zPos, int xLen, int zLen, int scale) {
        scale = Math.max(1, scale);

        String[] biomeNames = new String[xLen * zLen];
        int i = 0;//Java arrays start with 0

        for (int z = 0; z < zLen; ++z) {
            for (int x = 0; x < xLen; ++x) {
                Holder<Biome> biome = block.getLevel().getBiome(new BlockPos(xPos + x * scale, 0, zPos + z * scale));
                biomeNames[i++] = biome.unwrapKey().map(ResourceKey::location).map(String::valueOf).map(String::intern).orElse(null);
            }
        }

        return biomeNames;
    }

    @SuppressWarnings("rawtypes")
    @LuaFunction
    public final int setBiomeByteMapping(Map mapping) throws LuaException {
        byteMap.clear();
        @SuppressWarnings("unchecked")
        Map<Object, Object> objMapping = mapping;
        for (Entry<Object, Object> entry : objMapping.entrySet()) {
            Object key = entry.getKey();
            if (key instanceof String) {
                String biomeName = (String)entry.getKey();
                Holder<Biome> biome = getBiomeOrThrow(biomeName);
                Object val = entry.getValue();
                if (val instanceof Number) {
                    Number num = (Number)val;
                    byte bVal = num.byteValue();
                    byteMap.put(biome, bVal);
                }
            }
        }
        return 0;
    }

    @LuaFunction
    public final String getBiomeByteArray(int xPos, int zPos, int xLen, int zLen, int scale) {
        scale = Math.max(1, scale);

        byte[] bytes = new byte[xLen * zLen];
        int i = 0;

        for (int z = 0; z < zLen; ++z) {
            for (int x = 0; x < xLen; ++x) {
                Holder<Biome> biome = block.getLevel().getBiome(new BlockPos(xPos + x * scale, 0, zPos + z * scale));
                byte bVal = byteMap.getOrDefault(biome, (byte)0);
                bytes[i++] = bVal;
            }
        }
        return new String(bytes, StandardCharsets.ISO_8859_1);
    }

    @LuaFunction
    public final int getYTop(int x, int z, boolean solid) {
        return block.getLevel().getHeight(solid ? Heightmap.Types.MOTION_BLOCKING : Heightmap.Types.WORLD_SURFACE, x, z);
    }

    @LuaFunction
    public final Object getYTopArray(int xPos, int zPos, int xLen, int zLen, int scale, boolean solid) {
        scale = Mth.clamp(scale, 1, scale);

        Map<Integer, Integer> heights = new HashMap<>();

        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
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
    public final float getTemperature(int x, int y, int z) {
        BlockPos pos = new BlockPos(x, y, z);
        return block.getLevel().getBiome(pos).value().getTemperature(pos);
    }

//    @LuaFunction
//    public final void generateChunk(int x, int z) {
//        IChunkLightProvider chunkSource = block.getLevel().getChunkSource();
//        if (chunkSource instanceof ServerChunkProvider) {
//            ServerChunkProvider chunkProvider = (ServerChunkProvider) chunkSource;
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
//        }
//    }

}