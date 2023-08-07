package com.ferreusveritas.mcf;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CreativeModeTabs {

    public static final CreativeModeTab MAIN = new CreativeModeTab(MCF.MOD_ID) {
        @OnlyIn(Dist.CLIENT)
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(Registry.TERRAFORMER_ITEM.get());
        }
    };

}