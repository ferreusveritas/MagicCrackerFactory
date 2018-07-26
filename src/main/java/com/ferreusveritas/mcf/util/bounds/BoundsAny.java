package com.ferreusveritas.mcf.util.bounds;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

public class BoundsAny extends BoundsBase {

	public BoundsAny() { }
	
	public BoundsAny(NBTTagCompound nbt) {
		super(nbt);
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
