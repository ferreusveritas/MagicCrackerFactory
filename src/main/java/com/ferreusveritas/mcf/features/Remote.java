package com.ferreusveritas.mcf.features;

import com.ferreusveritas.mcf.FeatureableMod;
import com.ferreusveritas.mcf.ModConstants;
import com.ferreusveritas.mcf.blocks.BlockMapGuard;
import com.ferreusveritas.mcf.blocks.BlockPeripheral;
import com.ferreusveritas.mcf.blocks.BlockRemoteButton;
import com.ferreusveritas.mcf.blocks.PeripheralType;
import com.ferreusveritas.mcf.items.UniversalRemote;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemMultiTexture;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;

@Mod.EventBusSubscriber(modid = ModConstants.MODID)
public class Remote implements IFeature {
	
	public static Block blockRemoteReceiver;
	public static Block blockRemoteButton;
	public static Block blockMapGuard;
	public static UniversalRemote universalRemote;
	
	private Remote() { }
	
	@SubscribeEvent
	public static void register(final FeatureableMod.FeatureRegistryEvent event) {
		event.regFeature(new Remote());
	}
	
	@Override
	public void preInit() { }
	
	@Override
	public void createBlocks() {
		blockRemoteReceiver = new BlockPeripheral(PeripheralType.REMOTERECEIVER);
		blockRemoteButton = new BlockRemoteButton();
		blockMapGuard = new BlockMapGuard();
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
		registry.register(blockRemoteButton);
		registry.register(blockMapGuard);
	}
	
	@Override
	public void registerItems(IForgeRegistry<Item> registry) {
		registry.register(universalRemote);
		registry.register( new ItemBlock(blockRemoteReceiver).setRegistryName(blockRemoteReceiver.getRegistryName()) );
		registry.register( new ItemBlock(blockRemoteButton).setRegistryName(blockRemoteButton.getRegistryName()) );
		registry.register( new ItemMultiTexture(blockMapGuard, blockMapGuard, stack -> stack.getItemDamage() == 0 ? "unlit" : "lit").setRegistryName(blockMapGuard.getRegistryName()));
	}
	
	@Override
	public void registerRecipes(IForgeRegistry<IRecipe> registry) { }
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerModels() {
		ModelLoader.setCustomModelResourceLocation(universalRemote, 0, new ModelResourceLocation(universalRemote.getRegistryName(), "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(blockRemoteReceiver), 0, new ModelResourceLocation(blockRemoteReceiver.getRegistryName(), "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(blockRemoteButton), 0, new ModelResourceLocation(blockRemoteButton.getRegistryName(), "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(blockMapGuard), 0, new ModelResourceLocation(blockMapGuard.getRegistryName() + "_unlit", "inventory"));
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(blockMapGuard), 1, new ModelResourceLocation(blockMapGuard.getRegistryName() + "_lit", "inventory"));
	}
	
}
