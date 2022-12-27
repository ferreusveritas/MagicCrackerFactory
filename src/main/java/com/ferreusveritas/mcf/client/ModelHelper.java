package com.ferreusveritas.mcf.client;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModelHelper {

    public static void registerColorHandler(Block block, IBlockColor blockColor) {
        Minecraft.getInstance().getBlockColors().register(blockColor, block);
    }

    public static void registerColorHandler(Item item, IItemColor itemColor) {
        Minecraft.getInstance().getItemColors().register(itemColor, item);
    }

}
