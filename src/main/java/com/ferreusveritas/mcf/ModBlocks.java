package com.ferreusveritas.mcf;

import com.ferreusveritas.mcf.blocks.BlockCartographer;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraftforge.registries.IForgeRegistry;

public class ModBlocks {
	
	public static Block blockCartographer;
	
	public static void preInit() {
		
		blockCartographer = new BlockCartographer(Material.IRON);
		
	}

	public static void registerBlocks(IForgeRegistry<Block> registry) {
		
		registry.register(blockCartographer);
		
	}

}
