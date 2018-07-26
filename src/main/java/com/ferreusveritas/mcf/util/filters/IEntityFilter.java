package com.ferreusveritas.mcf.util.filters;

import net.minecraft.entity.EntityLivingBase;

public interface IEntityFilter {
	
	public static IEntityFilter INVALID = new EntityFilterNone();
	
	String getType();
	boolean isEntityDenied(EntityLivingBase entity);
	String getFilterData();
}
