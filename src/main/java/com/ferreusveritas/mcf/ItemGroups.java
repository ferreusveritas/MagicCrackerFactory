package com.ferreusveritas.mcf;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ItemGroups {

    public static final ItemGroup MAIN = new ItemGroup(MCF.MOD_ID) {
        @OnlyIn(Dist.CLIENT)
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(Registry.TERRAFORMER_ITEM.get());
        }
    };

}