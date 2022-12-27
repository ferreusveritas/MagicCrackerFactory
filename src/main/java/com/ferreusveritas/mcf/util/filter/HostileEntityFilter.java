package com.ferreusveritas.mcf.util.filter;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.IMob;

public class HostileEntityFilter implements EntityFilter {

    @Override
    public String getType() {
        return "hostile";
    }

    @Override
    public boolean isEntityDenied(LivingEntity entity) {
        return entity instanceof IMob;
    }

    @Override
    public String getFilterData() {
        return "";
    }

}
