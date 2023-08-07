package com.ferreusveritas.mcf.util.bounds;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;

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
    public AABB getAABB() {
        return new AABB(0, 0, 0, 0, 0, 0);
    }

}
