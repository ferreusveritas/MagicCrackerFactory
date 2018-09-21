package com.ferreusveritas.mcf.proxy;

import com.ferreusveritas.dynamictrees.api.client.ModelHelper;
import com.ferreusveritas.mcf.features.Remote;

import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

public class ClientProxy extends CommonProxy {
	
	public CreativeTabs findCreativeTab(String label) {
		for(CreativeTabs iTab: CreativeTabs.CREATIVE_TAB_ARRAY) {
			if(iTab.getTabLabel().equals(label)) {
				return iTab;
			}
		}
		return null;
	}
	
	@Override
	public void init() {
		super.init();
		registerColorHandlers();
	}
	
	public void registerColorHandlers() {
		
		//Register Universal Remote and Colorizer
		ModelHelper.regColorHandler(Remote.universalRemote, new IItemColor() {
			@Override
			public int colorMultiplier(ItemStack stack, int tintIndex) {
				return tintIndex == 1 ? Remote.universalRemote.getColor(stack) : 0xFFFFFFFF;
			}
		});
		
	}

}
