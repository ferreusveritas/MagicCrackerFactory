package com.ferreusveritas.mcf.features;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;

public interface IFeature {
	
	void preInit();
	void createBlocks();
	void createItems();
	void registerEvents();
	void init();
	void postInit();
	void onLoadComplete();
	void registerBlocks(IForgeRegistry<Block> registry);
	void registerItems(IForgeRegistry<Item> registry);
	void registerRecipes(IForgeRegistry<IRecipe> registry);
	
	@SideOnly(Side.CLIENT)
	void registerModels();
}
