package com.ferreusveritas.mcf.util.bounds;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

public class AnyBounds extends BaseBounds {

	public AnyBounds() { }
	
	public AnyBounds(NBTTagCompound nbt) { }
	
	@Override
	public boolean inBounds(BlockPos pos) {
		return true;
	}

	@Override
	public String getBoundType() {
		return "any";
	}

	@Override
	public Object[] toLuaObject() {
		Map<String, Object> contents = new HashMap<>();
		contents.put("type", getBoundType());
		return new Object[] { contents }; 
	}

	@Override
	public AxisAlignedBB getAABB() {
		return null;
	}
	
}
