package com.ferreusveritas.mcf.util.bounds;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.phys.AABB;

public class AnyBounds extends Bounds {

    public AnyBounds() {
    }

    public AnyBounds(CompoundTag tag) {
        super(tag);
    }

    @Override
    public boolean inBounds(BlockPos pos) {
        return true;
    }

    @Override
    public String getBoundType() {
        return "any";
    }

    @Override
    public AABB getAABB() {
        return null;
    }

}
