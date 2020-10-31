package com.ferreusveritas.mcf.features;

import com.ferreusveritas.mcf.FeatureableMod;
import com.ferreusveritas.mcf.ModConstants;
import com.ferreusveritas.mcf.items.CommandBlockRing;
import com.ferreusveritas.mcf.items.CommandChunkRing;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;

@Mod.EventBusSubscriber(modid = ModConstants.MODID)
public class Rings implements IFeature {
	
	public static CommandChunkRing commandChunkRing;
	public static CommandBlockRing commandBlockRing;
	
	private Rings() { }
	
	@SubscribeEvent
	public static void register(final FeatureableMod.FeatureRegistryEvent event) {
		event.regFeature(new Rings());
	}
	
	@Override
	public void preInit() { }
	
	@Override
	public void createBlocks() { }
	
	@Override
	public void createItems() {
		commandChunkRing = new CommandChunkRing();
		commandBlockRing = new CommandBlockRing();
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
	public void registerBlocks(IForgeRegistry<Block> registry) { }
	
	@Override
	public void registerItems(IForgeRegistry<Item> registry) {
		registry.register(commandChunkRing);
		registry.register(commandBlockRing);
	}
	
	@Override
	public void registerRecipes(IForgeRegistry<IRecipe> registry) { }
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerModels() {
		ModelLoader.setCustomModelResourceLocation(commandChunkRing, 0, new ModelResourceLocation(commandChunkRing.getRegistryName(), "inventory"));
		ModelLoader.setCustomModelResourceLocation(commandBlockRing, 0, new ModelResourceLocation(commandBlockRing.getRegistryName(), "inventory"));
	}
	
}
