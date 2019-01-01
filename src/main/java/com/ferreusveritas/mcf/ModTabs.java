package com.ferreusveritas.mcf;

import com.ferreusveritas.mcf.features.Terraformer;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ModTabs {
	
	public static final CreativeTabs mcfTab = new CreativeTabs(ModConstants.MODID) {
		@SideOnly(Side.CLIENT)
		@Override
		public ItemStack getTabIconItem() {
			return new ItemStack(ItemBlock.getItemFromBlock(Terraformer.blockTerraformer));
		}
	};
	
}