package com.ferreusveritas.mcf.util;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;

public abstract class BaseBounds {
	
	public static final BaseBounds INVALID = new BaseBounds() {
		@Override public boolean inBounds(BlockPos pos) { return false; }
		@Override public String getBoundType() { return "null"; }
		@Override
		Object[] toLuaObject() {
			Map<String, Object> contents = new HashMap<>();
			contents.put("type", getBoundType());
			return new Object[] { contents };
		}
	};
	
	abstract boolean inBounds(BlockPos pos);
	abstract public String getBoundType();
	abstract Object[] toLuaObject();
	
	public NBTTagCompound toNBTTagCompound() {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setString("type", getBoundType());
		return nbt;
	}
	
}
