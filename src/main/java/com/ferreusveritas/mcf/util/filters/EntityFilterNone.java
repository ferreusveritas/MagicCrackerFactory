package com.ferreusveritas.mcf.util.filters;

import net.minecraft.entity.EntityLivingBase;

public class EntityFilterNone implements IEntityFilter {

	@Override
	public String getType() {
		return "none";
	}

	@Override
	public boolean isEntityDenied(EntityLivingBase entity) {
		return false;
	}

	@Override
	public String getFilterData() {
		return "";
	}
	
}
