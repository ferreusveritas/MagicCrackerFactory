package com.ferreusveritas.mcf.features;

import java.util.Collections;
import java.util.Map;

import com.ferreusveritas.mcf.FeatureableMod;
import com.ferreusveritas.mcf.ModConstants;
import com.ferreusveritas.mcf.blocks.BlockLight;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.IStateMapper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemMultiTexture;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;

@Mod.EventBusSubscriber(modid = ModConstants.MODID)
public class Light implements IFeature {
	
	public static Block blockLight;
	
	private Light() { }
	
	@SubscribeEvent
	public static void register(final FeatureableMod.FeatureRegistryEvent event) {
		event.regFeature(new Light());
	}
	
	@Override
	public void preInit() { }
	
	@Override
	public void createBlocks() {
		blockLight = new BlockLight();
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
		registry.register(blockLight);
	}
	
	@Override
	public void registerItems(IForgeRegistry<Item> registry) {
		registry.register(new ItemMultiTexture(blockLight, blockLight, new ItemMultiTexture.Mapper() {
			public String apply(ItemStack stack) {
				return "" + stack.getMetadata();
			}
		}).setRegistryName(blockLight.getRegistryName()));
	}
	
	@Override
	public void registerRecipes(IForgeRegistry<IRecipe> registry) { }
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerModels() {
		ModelLoader.setCustomStateMapper(blockLight, new IStateMapper() {
			@Override
			public Map<IBlockState, ModelResourceLocation> putStateModelLocations(Block blockIn) {
				return Collections.emptyMap();
			}
		});
		
		for(int meta = 0; meta < 16; meta++) {
			ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(blockLight), meta, new ModelResourceLocation(blockLight.getRegistryName() + "_" + meta, "inventory"));
		}
	}
	
}
