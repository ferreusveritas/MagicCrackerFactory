package com.ferreusveritas.mcf.util.biome;

import com.google.common.collect.Maps;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.world.World;
import net.minecraft.world.biome.*;
import net.minecraft.world.chunk.Chunk;

import java.util.Map;

public interface BiomeSetter {

    Map<IBiomeMagnifier, BiomeSetter> SETTERS = Util.make(() -> {
        Map<IBiomeMagnifier, BiomeSetter> setters = Maps.newHashMap();
        setters.put(DefaultBiomeMagnifier.INSTANCE, DefaultMagnifierBiomeSetter.INSTANCE);
        setters.put(FuzzedBiomeMagnifier.INSTANCE, FuzzedMagnifierBiomeSetter.INSTANCE);
        setters.put(ColumnFuzzedBiomeMagnifier.INSTANCE, ColumnFuzzedMagnifierBiomeSetter.INSTANCE);
        return setters;
    });

    default Chunk setBiome(long seed, int x, int y, int z, World world, Biome biome) {
        Vector3i biomePos = getBiomePos(seed, x, y, z);
        Chunk chunk = world.getChunk(biomePos.getX() >> 2, biomePos.getZ() >> 2);
        chunk.getBiomes().biomes[getBiomeIndex(biomePos)] = biome;
        return chunk;
    }

    /**
     * Uses code from {@link BiomeContainer#getNoiseBiome(int, int, int)} to determine the biome index for the
     * position given.
     */
    default int getBiomeIndex(Vector3i biomePos) {
        int i = biomePos.getX() & BiomeContainer.HORIZONTAL_MASK;
        int j = MathHelper.clamp(biomePos.getY(), 0, BiomeContainer.VERTICAL_MASK);
        int k = biomePos.getZ() & BiomeContainer.HORIZONTAL_MASK;
        return j << 2 + 2 | k << 2 | i;
    }

    Vector3i getBiomePos(long seed, int x, int y, int z);

}
