package com.ferreusveritas.mcf;

import com.ferreusveritas.mcf.blocks.BlockCartographer;
import com.ferreusveritas.mcf.blocks.BlockTerraformer;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraftforge.registries.IForgeRegistry;

public class ModBlocks {
	
	public static Block blockCartographer;
	public static Block blockTerraformer;
	
	public static void preInit() {
		
		blockCartographer = new BlockCartographer(Material.IRON);
		blockTerraformer = new BlockTerraformer(Material.IRON);
		
	}
	
	public static void registerBlocks(IForgeRegistry<Block> registry) {
		
		registry.register(blockCartographer);
		registry.register(blockTerraformer);
		
	}

}
