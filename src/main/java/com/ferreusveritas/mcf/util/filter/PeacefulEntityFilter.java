package com.ferreusveritas.mcf.util.filter;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.IMob;

public class PeacefulEntityFilter implements EntityFilter {

    @Override
    public String getType() {
        return "peaceful";
    }

    @Override
    public boolean isEntityDenied(LivingEntity entity) {
        return !(entity instanceof IMob);
    }

    @Override
    public String getFilterData() {
        return "";
    }

}
