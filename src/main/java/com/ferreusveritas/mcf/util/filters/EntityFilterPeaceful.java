package com.ferreusveritas.mcf.util.filters;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySlime;

public class EntityFilterPeaceful implements IEntityFilter {

	@Override
	public String getType() {
		return "peaceful";
	}
	
	@Override
	public boolean isEntityDenied(EntityLivingBase entity) {
		return !(entity instanceof EntityMob) && !(entity instanceof EntitySlime);
	}

	@Override
	public String getFilterData() {
		return "";
	}
	
}
