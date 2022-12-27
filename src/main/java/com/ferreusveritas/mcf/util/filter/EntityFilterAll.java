package com.ferreusveritas.mcf.util.filter;

import net.minecraft.entity.LivingEntity;

public class EntityFilterAll implements EntityFilter {

    @Override
    public String getType() {
        return "all";
    }

    @Override
    public boolean isEntityDenied(LivingEntity entity) {
        return true;
    }

    @Override
    public String getFilterData() {
        return "";
    }

}
