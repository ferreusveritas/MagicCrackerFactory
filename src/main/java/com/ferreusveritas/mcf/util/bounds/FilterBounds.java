package com.ferreusveritas.mcf.util.bounds;

import java.util.Map;

import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.registry.EntityEntry;

public class FilterBounds extends BaseBounds {
	
	public BaseBounds baseBounds;
	public EntityEntry entityEntry;
	
	public FilterBounds(BaseBounds baseBounds, EntityEntry deniedEntity) {
		this.baseBounds = baseBounds;
		this.entityEntry = deniedEntity;
	}
	
	@Override
	public boolean inBounds(BlockPos pos) {
		return baseBounds.inBounds(pos);
	}
	
	@Override
	public String getBoundType() {
		return "filtered";
	}
	
	public boolean isEntityAllowed(Entity entity) {
		return !isEntityDenied(entity);
	}
	
	public boolean isEntityDenied(Entity entity) {
		return entityEntry.getEntityClass().isInstance(entity);
	}
	
	@Override
	public Object[] toLuaObject() {
		Map<String, Object> contents = (Map<String, Object>) baseBounds.toLuaObject()[0]; //<-- This is atrocious and not at all type-safe
		ResourceLocation entityName = entityEntry.getRegistryName();
		contents.put("entity", entityName.toString());
		return new Object[] { contents }; 
	}
	
	@Override
	public AxisAlignedBB getAABB() {
		return baseBounds.getAABB();
	}
	
}
