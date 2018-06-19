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

public class Terraformer implements IFeature {
	
	public static Block blockTerraformer;
	
	@Override
	public void preInit() { }
	
	@Override
	public void createBlocks() {
		blockTerraformer = new BlockPeripheral(PeripheralType.TERRAFORMER);
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
		registry.register(blockTerraformer);
	}
	
	@Override
	public void registerItems(IForgeRegistry<Item> registry) {
		registry.register( new ItemBlock(blockTerraformer).setRegistryName(blockTerraformer.getRegistryName()) );
	}
	
	@Override
	public void registerRecipes(IForgeRegistry<IRecipe> registry) { }
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerModels() {
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(blockTerraformer), 0, new ModelResourceLocation(blockTerraformer.getRegistryName(), "inventory"));
	}
	
}
