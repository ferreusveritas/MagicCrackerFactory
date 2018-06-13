package com.ferreusveritas.mcf.util;

import java.util.List;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;

public class DimBlockBounds extends BlockBounds {

	private int dim;
	
	public DimBlockBounds(BlockPos pos, int dim) {
		super(pos);
		this.dim = dim;
	}

	public DimBlockBounds(ChunkPos cPos, int dim) {
		super(cPos);
		this.dim = dim;
	}
	
	public DimBlockBounds(BlockBounds other, int dim) {
		super(other);
		this.dim = dim;
	}
	
	public DimBlockBounds(List<BlockPos> blockPosList, int dim) {
		super(blockPosList);
		this.dim = dim;
	}

	public int getDimension() {
		return dim;
	}

	public void setDimension(int dim) {
		this.dim = dim;
	}
	
	public boolean inBounds(BlockPos pos, int dim) {
		return this.dim == dim && super.inBounds(pos, 0);
	}
	
	@Override
	public int hashCode() {
		return super.hashCode() ^ (dim * 57257861);
	}
	
	@Override
	public boolean equals(Object obj) {
		return super.equals(obj) && this.dim == ((DimBlockBounds)obj).dim;
	}
}
