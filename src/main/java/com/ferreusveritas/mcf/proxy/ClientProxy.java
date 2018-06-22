package com.ferreusveritas.mcf.proxy;

import net.minecraft.creativetab.CreativeTabs;

public class ClientProxy extends CommonProxy {
	
	public CreativeTabs findCreativeTab(String label) {
		for(CreativeTabs iTab: CreativeTabs.CREATIVE_TAB_ARRAY) {
			if(iTab.getTabLabel().equals(label)) {
				return iTab;
			}
		}
		return null;
	}
	
}
