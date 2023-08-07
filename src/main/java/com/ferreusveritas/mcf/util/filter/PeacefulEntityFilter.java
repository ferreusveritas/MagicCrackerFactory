package com.ferreusveritas.mcf.util.filter;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;

public class PeacefulEntityFilter implements EntityFilter {

    @Override
    public String getType() {
        return "peaceful";
    }

    @Override
    public boolean isEntityDenied(LivingEntity entity) {
        return !(entity instanceof Enemy);
    }

    @Override
    public String getFilterData() {
        return "";
    }

}
