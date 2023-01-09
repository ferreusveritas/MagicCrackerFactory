package com.ferreusveritas.mcf.util.biome;

import net.minecraft.util.math.vector.Vector3i;

public class DefaultMagnifierBiomeSetter implements BiomeSetter {

    public static final BiomeSetter INSTANCE = new DefaultMagnifierBiomeSetter();

    private DefaultMagnifierBiomeSetter() {
    }

    @Override
    public Vector3i getBiomePos(long seed, int x, int y, int z) {
        return new Vector3i(x >> 2, y >> 2, z >> 2);
    }
}
