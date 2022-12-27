package com.ferreusveritas.mcf.util.filter;

import net.minecraft.entity.LivingEntity;

public class NoneEntityFilter implements EntityFilter {

    @Override
    public String getType() {
        return "none";
    }

    @Override
    public boolean isEntityDenied(LivingEntity entity) {
        return false;
    }

    @Override
    public String getFilterData() {
        return "";
    }

}
