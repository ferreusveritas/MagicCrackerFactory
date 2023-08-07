package com.ferreusveritas.mcf.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public final class ModelHelper {

    public static void registerColorHandler(Block block, BlockColor blockColor) {
        Minecraft.getInstance().getBlockColors().register(blockColor, block);
    }

    public static void registerColorHandler(Item item, ItemColor itemColor) {
        Minecraft.getInstance().getItemColors().register(itemColor, item);
    }

}
