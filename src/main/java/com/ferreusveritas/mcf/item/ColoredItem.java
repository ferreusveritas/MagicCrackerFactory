package com.ferreusveritas.mcf.item;

import net.minecraft.world.item.ItemStack;

public interface ColoredItem {

    int getColor(ItemStack stack, int tintIndex);

}
