package com.ferreusveritas.mcf;

import java.util.ArrayList;

import com.ferreusveritas.mcf.features.IFeature;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class FeatureableMod {

	public static ArrayList<IFeature> features = new ArrayList<>();
	
	public FeatureableMod() {
		setupFeatures();
	}
	
	protected abstract void setupFeatures();
	
	protected void addFeature(IFeature feature) {
		features.add(feature);
	}
	
	protected void addFeatures(IFeature ... features ) {
		for(IFeature feature : features) {
			addFeature(feature);
		}
	}
	
	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		features.forEach(i -> i.preInit());
		features.forEach(i -> i.createBlocks());
		features.forEach(i -> i.createItems());
		features.forEach(i -> i.registerEvents());
	}
	
	@Mod.EventHandler
	public void init(FMLInitializationEvent event) {
		features.forEach(i -> i.init());
	}
	
	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		features.forEach(i -> i.postInit());
	}
	
	@Mod.EventHandler
	public void onFMLLoadComplete(FMLLoadCompleteEvent event) {
		features.forEach(i -> i.onLoadComplete());
	}
	
	@Mod.EventBusSubscriber
	public static class RegistrationHandler {
		
		@SubscribeEvent
		public static void registerBlocks(RegistryEvent.Register<Block> event) {
			features.forEach(i -> i.registerBlocks(event.getRegistry()));
		}
		
		@SubscribeEvent
		public static void registerItems(RegistryEvent.Register<Item> event) {
			features.forEach(i -> i.registerItems(event.getRegistry()));
		}
		
		@SubscribeEvent
		public static void registerRecipes(RegistryEvent.Register<IRecipe> event) {
			features.forEach(i -> i.registerRecipes(event.getRegistry()));
		}
		
		@SubscribeEvent
		@SideOnly(Side.CLIENT)
		public static void registerModels(ModelRegistryEvent event) {
			features.forEach(i -> i.registerModels());
		}
	}
	
}
