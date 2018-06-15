package com.ferreusveritas.mcf.util;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;

public abstract class Bounds {
	
	public static final Bounds INVALID = new Bounds() {
		@Override public boolean inBounds(BlockPos pos) { return false; }
		@Override public String getBoundType() { return "null"; }
	};
	
	abstract boolean inBounds(BlockPos pos);
	abstract public String getBoundType();
	
	public NBTTagCompound toNBTTagCompound() {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setString("type", getBoundType());
		return nbt;
	}
	
}
