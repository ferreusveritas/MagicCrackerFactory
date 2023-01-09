package com.ferreusveritas.mcf.util.biome;

import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.world.biome.BiomeManager;
import net.minecraft.world.biome.FuzzedBiomeMagnifier;

/**
 * Sets biomes in levels that use the {@link FuzzedBiomeMagnifier}.
 */
public class FuzzedMagnifierBiomeSetter implements BiomeSetter {

    public static final BiomeSetter INSTANCE = new FuzzedMagnifierBiomeSetter();

    protected FuzzedMagnifierBiomeSetter() {
    }

    /**
     * Uses code from {@link FuzzedBiomeMagnifier#getBiome(long, int, int, int, BiomeManager.IBiomeReader)} to determine the
     * parameters to pass to {@link net.minecraft.world.biome.BiomeContainer#getNoiseBiome(int, int, int)}.
     */
    @Override
    public Vector3i getBiomePos(long seed, int x, int y, int z) {
        int i = x - 2;
        int j = y - 2;
        int k = z - 2;
        int l = i >> 2;
        int i1 = j >> 2;
        int j1 = k >> 2;
        double d0 = (double)(i & 3) / 4.0D;
        double d1 = (double)(j & 3) / 4.0D;
        double d2 = (double)(k & 3) / 4.0D;
        double[] adouble = new double[8];

        for(int k1 = 0; k1 < 8; ++k1) {
            boolean flag = (k1 & 4) == 0;
            boolean flag1 = (k1 & 2) == 0;
            boolean flag2 = (k1 & 1) == 0;
            int l1 = flag ? l : l + 1;
            int i2 = flag1 ? i1 : i1 + 1;
            int j2 = flag2 ? j1 : j1 + 1;
            double d3 = flag ? d0 : d0 - 1.0D;
            double d4 = flag1 ? d1 : d1 - 1.0D;
            double d5 = flag2 ? d2 : d2 - 1.0D;
            adouble[k1] = FuzzedBiomeMagnifier.getFiddledDistance(seed, l1, i2, j2, d3, d4, d5);
        }

        int k2 = 0;
        double d6 = adouble[0];

        for(int l2 = 1; l2 < 8; ++l2) {
            if (d6 > adouble[l2]) {
                k2 = l2;
                d6 = adouble[l2];
            }
        }

        int i3 = (k2 & 4) == 0 ? l : l + 1;
        int j3 = (k2 & 2) == 0 ? i1 : i1 + 1;
        int k3 = (k2 & 1) == 0 ? j1 : j1 + 1;
        return new Vector3i(i3, j3, k3);
    }

}
