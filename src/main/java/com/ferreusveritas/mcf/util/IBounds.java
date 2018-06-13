package com.ferreusveritas.mcf.util;

import net.minecraft.util.math.BlockPos;

public interface IBounds {

	public static final IBounds INVALID = new IBounds() {
		@Override public boolean inBounds(BlockPos pos, int dim) { return false; }
	};

	
	boolean inBounds(BlockPos pos, int dim);
}
