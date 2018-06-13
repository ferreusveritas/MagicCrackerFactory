package com.ferreusveritas.mcf.features;

import java.util.ArrayList;

import com.ferreusveritas.mcf.event.SecurityHandler;
import com.ferreusveritas.mcf.util.DimBlockBounds;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;

public class Security implements IFeature {
	
	@Override
	public void preInit() { }

	@Override
	public void createBlocks() { }

	@Override
	public void createItems() { }

	@Override
	public void registerEvents() {
		MinecraftForge.EVENT_BUS.register(new SecurityHandler());
	}
	
	@Override
	public void init() { 
		
		ArrayList<BlockPos> corners = new ArrayList<>();
		corners.add(new BlockPos(-128, 0, -128));
		corners.add(new BlockPos(128, 256, 128));
		
		DimBlockBounds bb = new DimBlockBounds(corners, 0);
		SecurityHandler.addExplodeDenyBounds(bb);
		SecurityHandler.addSpawnDenyBounds(bb);
		SecurityHandler.addBreakDenyBounds(bb);
		SecurityHandler.addPlaceDenyBounds(bb);
		
	}
	
	@Override
	public void postInit() { }
	
	@Override
	public void onLoadComplete() { }

	@Override
	public void registerBlocks(IForgeRegistry<Block> event) { }
	
	@Override
	public void registerItems(IForgeRegistry<Item> registry) { }
	
	@Override
	public void registerRecipes(IForgeRegistry<IRecipe> registry) { }

	@Override
	@SideOnly(Side.CLIENT)
	public void registerModels() { }
	
}
