package com.ferreusveritas.mcf.util.filter;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;

public class HostileEntityFilter implements EntityFilter {

    @Override
    public String getType() {
        return "hostile";
    }

    @Override
    public boolean isEntityDenied(LivingEntity entity) {
        return entity instanceof Enemy;
    }

    @Override
    public String getFilterData() {
        return "";
    }

}
