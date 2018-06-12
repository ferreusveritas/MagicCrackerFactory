package com.ferreusveritas.mcf.features;

import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;

public interface IFeature {
	
	void preInit();
	void init();
	void postInit();
	void onLoadComplete();
	void registerItems(IForgeRegistry<Item> event);
	void registerRecipes(IForgeRegistry<IRecipe> registry);
	
	@SideOnly(Side.CLIENT)
	void registerModels();
}
