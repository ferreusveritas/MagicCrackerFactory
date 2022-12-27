package com.ferreusveritas.mcf.util.bounds;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

public class AnyBounds extends Bounds {

    public AnyBounds() {
    }

    public AnyBounds(CompoundNBT tag) {
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
    public AxisAlignedBB getAABB() {
        return null;
    }

}
