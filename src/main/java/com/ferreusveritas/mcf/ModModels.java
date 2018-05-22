package com.ferreusveritas.mcf;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;

public class ModModels {

	public static void registerModels(ModelRegistryEvent event) {
		Block cartographer = Block.REGISTRY.getObject(new ResourceLocation(ModConstants.MODID, "cartographer"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(cartographer), 0, new ModelResourceLocation(cartographer.getRegistryName(), "inventory"));
	}

}
