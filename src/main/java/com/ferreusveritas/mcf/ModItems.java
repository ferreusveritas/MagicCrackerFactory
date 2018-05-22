package com.ferreusveritas.mcf;

import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.registries.IForgeRegistry;

public class ModItems {

	public static void registerItems(IForgeRegistry<Item> registry) {
		registry.register( new ItemBlock(ModBlocks.blockCartographer) .setRegistryName(ModBlocks.blockCartographer.getRegistryName()) );
	}

}
