package com.ferreusveritas.mcf.util.filters;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class EntityFilterEntity implements IEntityFilter {

	EntityEntry entry;
	String data;
	Class<? extends EntityLivingBase> entityClass;

	@Override
	public String getType() {
		return "entity";
	}
	
	public EntityFilterEntity(String entityLocation) {
		entry = ForgeRegistries.ENTITIES.getValue(new ResourceLocation(entityLocation));
		if(entry != null && entry.getClass().isInstance(EntityLivingBase.class)) {
			entityClass = (Class<? extends EntityLivingBase>) entry.getEntityClass();
			data = entityLocation;
		} else {
			entry = null;
			data = "";
		}
	}
	
	@Override
	public boolean isEntityDenied(EntityLivingBase entity) {
		return entry != null ? entityClass.isInstance(entity) : false;
	}

	@Override
	public String getFilterData() {
		return data;
	}
	
}
