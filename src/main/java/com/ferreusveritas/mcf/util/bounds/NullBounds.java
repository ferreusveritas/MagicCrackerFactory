package com.ferreusveritas.mcf.util.bounds;

import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

public class NullBounds extends Bounds {

    @Override
    public boolean inBounds(BlockPos pos) {
        return false;
    }

    @Override
    public String getBoundType() {
        return "null";
    }

    @Override
    public AxisAlignedBB getAABB() {
        return new AxisAlignedBB(0, 0, 0, 0, 0, 0);
    }

}
