package com.ferreusveritas.mcf.features;

import com.ferreusveritas.mcf.blocks.BlockPeripheral;
import com.ferreusveritas.mcf.blocks.PeripheralType;
import com.ferreusveritas.mcf.items.UniversalRemote;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;

public class Remote implements IFeature {
	
	public static Block blockRemoteReceiver;
	public static Item universalRemote;
	
	@Override
	public void preInit() { }
	
	@Override
	public void createBlocks() {
		blockRemoteReceiver = new BlockPeripheral(PeripheralType.REMOTERECEIVER);
	}

	@Override
	public void createItems() {
		universalRemote = new UniversalRemote();
	}

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
		registry.register(blockRemoteReceiver);
	}
	
	@Override
	public void registerItems(IForgeRegistry<Item> registry) {
		registry.register(universalRemote);
		registry.register( new ItemBlock(blockRemoteReceiver).setRegistryName(blockRemoteReceiver.getRegistryName()) );
	}
	
	@Override
	public void registerRecipes(IForgeRegistry<IRecipe> registry) { }
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerModels() {
		ModelLoader.setCustomModelResourceLocation(universalRemote, 0, new ModelResourceLocation(universalRemote.getRegistryName(), "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(blockRemoteReceiver), 0, new ModelResourceLocation(blockRemoteReceiver.getRegistryName(), "inventory"));
	}
	
}