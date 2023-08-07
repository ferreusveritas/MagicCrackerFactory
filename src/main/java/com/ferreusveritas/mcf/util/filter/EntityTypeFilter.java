package com.ferreusveritas.mcf.util.filter;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.registries.ForgeRegistries;

public class EntityTypeFilter implements EntityFilter {

    EntityType<?> entityType;
    String typeName;

    public EntityTypeFilter(String typeName) {
        this.typeName = typeName;
        this.entityType = ForgeRegistries.ENTITIES.getValue(new ResourceLocation(typeName));
    }

    @Override
    public String getType() {
        return "entity";
    }

    @Override
    public boolean isEntityDenied(LivingEntity entity) {
        return this.entityType != null && entity.getType() == this.entityType;
    }

    @Override
    public String getFilterData() {
        return typeName;
    }

}
