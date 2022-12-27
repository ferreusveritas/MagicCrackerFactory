package com.ferreusveritas.mcf.item;

import net.minecraft.item.ItemStack;

public interface ColoredItem {

    int getColor(ItemStack stack, int tintIndex);

}
