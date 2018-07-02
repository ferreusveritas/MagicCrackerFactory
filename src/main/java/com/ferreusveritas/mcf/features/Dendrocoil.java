package com.ferreusveritas.mcf.features;

import com.ferreusveritas.mcf.blocks.BlockPeripheral;
import com.ferreusveritas.mcf.blocks.PeripheralType;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;

public class Dendrocoil implements IFeature {
	
	public static Block blockDendrocoil;
	
	@Override
	public void preInit() { }
	
	@Override
	public void createBlocks() {
		blockDendrocoil = new BlockPeripheral(PeripheralType.DENDROCOIL);
	}

	@Override
	public void createItems() { }

	@Override
	public void registerEvents() { }
	
	@Override
	public void init() { }
	
	@Override
	public void postInit() { }
	
	@Override
	public void onLoadComplete() { }
	
	@Override
	public void registerBlocks(IForgeRegistry<Block> registry) {
		registry.register(blockDendrocoil);
	}
	
	@Override
	public void registerItems(IForgeRegistry<Item> registry) {
		registry.register( new ItemBlock(blockDendrocoil).setRegistryName(blockDendrocoil.getRegistryName()) );
	}
	
	@Override
	public void registerRecipes(IForgeRegistry<IRecipe> registry) { }
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerModels() {
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(blockDendrocoil), 0, new ModelResourceLocation(blockDendrocoil.getRegistryName(), "inventory"));
	}
	
}