package com.ferreusveritas.mcf.util.filters;

import net.minecraft.entity.EntityLivingBase;

public class EntityFilterAll implements IEntityFilter {

	@Override
	public String getType() {
		return "all";
	}

	@Override
	public boolean isEntityDenied(EntityLivingBase entity) {
		return true;
	}

	@Override
	public String getFilterData() {
		return "";
	}
	
}
