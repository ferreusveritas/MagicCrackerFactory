package com.ferreusveritas.mcf.util.bounds;

import java.util.HashMap;
import java.util.Map;

import com.ferreusveritas.mcf.util.filters.EntityFilterSet;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

public abstract class BoundsBase {
	
	public static BoundsBase INVALID = new BoundsNull();
	
	private EntityFilterSet filterSet = new EntityFilterSet();
	
	public abstract boolean inBounds(BlockPos pos);
	public abstract String getBoundType();
	public abstract AxisAlignedBB getAABB();
	
	public BoundsBase() { }
	
	public BoundsBase(NBTTagCompound nbt) {
		filterSet.loadFilters(nbt);
	}
	
	public NBTTagCompound toNBTTagCompound() {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setString("type", getBoundType());
		nbt.setTag("filters", filterSet.saveFilters());
		return nbt;
	}
	
	public Map<String, Object> collectLuaData() {
		Map<String, Object> contents = new HashMap<>();
		contents.put("type", getBoundType());
		contents.put("filters", filterSet.filtersToLuaObject());
		return contents;
	}
	
	public Object[] toLuaObject() {
		return new Object[] { collectLuaData() };
	}
	
	public EntityFilterSet getFilterSet() {
		return filterSet;
	}
	
}
