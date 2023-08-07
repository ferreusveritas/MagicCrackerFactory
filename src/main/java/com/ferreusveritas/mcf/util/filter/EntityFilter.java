package com.ferreusveritas.mcf.util.filter;

import net.minecraft.world.entity.LivingEntity;

public interface EntityFilter {

    EntityFilter INVALID = new NoneEntityFilter();

    String getType();

    boolean isEntityDenied(LivingEntity entity);

    String getFilterData();
}
