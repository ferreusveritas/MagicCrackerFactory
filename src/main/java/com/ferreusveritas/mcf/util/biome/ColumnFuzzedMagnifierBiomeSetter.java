package com.ferreusveritas.mcf.util.biome;

import net.minecraft.util.math.vector.Vector3i;

public class ColumnFuzzedMagnifierBiomeSetter extends FuzzedMagnifierBiomeSetter {

    public static final BiomeSetter INSTANCE = new ColumnFuzzedMagnifierBiomeSetter();

    private ColumnFuzzedMagnifierBiomeSetter() {
        super();
    }

    @Override
    public Vector3i getBiomePos(long seed, int x, int y, int z) {
        return super.getBiomePos(seed, x, 0, z);
    }
}
