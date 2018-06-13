package com.ferreusveritas.mcf;

import java.util.ArrayList;

import com.ferreusveritas.mcf.features.IFeature;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.event.FMLStateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class FeatureableMod {

	public ArrayList<IFeature> features = new ArrayList<>();
	
	public FeatureableMod() {
		MinecraftForge.EVENT_BUS.register(new RegistrationHandler());
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
	
	public void stateEvent(FMLStateEvent event) {
		switch(event.getModState()) {
			case PREINITIALIZED: 
				features.forEach(i -> i.preInit());
				features.forEach(i -> i.createBlocks());
				features.forEach(i -> i.createItems());
				features.forEach(i -> i.registerEvents());
				break;
			case INITIALIZED:
				features.forEach(i -> i.init());
				break;
			case POSTINITIALIZED:
				features.forEach(i -> i.postInit());
				break;
			case AVAILABLE:
				features.forEach(i -> i.onLoadComplete());
				break;
			default: break;
		}
	}
		
	public class RegistrationHandler {
		
		@SubscribeEvent
		public void registerBlocks(RegistryEvent.Register<Block> event) {
			features.forEach(i -> i.registerBlocks(event.getRegistry()));
		}
		
		@SubscribeEvent
		public void registerItems(RegistryEvent.Register<Item> event) {
			features.forEach(i -> i.registerItems(event.getRegistry()));
		}
		
		@SubscribeEvent
		public void registerRecipes(RegistryEvent.Register<IRecipe> event) {
			features.forEach(i -> i.registerRecipes(event.getRegistry()));
		}
		
		@SubscribeEvent
		@SideOnly(Side.CLIENT)
		public void registerModels(ModelRegistryEvent event) {
			features.forEach(i -> i.registerModels());
		}
	}
	
}
