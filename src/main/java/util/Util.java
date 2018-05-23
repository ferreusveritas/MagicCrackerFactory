package util;

import net.minecraft.creativetab.CreativeTabs;

public class Util {

	public static CreativeTabs findCreativeTab(String label) {
		for(CreativeTabs iTab: CreativeTabs.CREATIVE_TAB_ARRAY) {
			if(iTab.getTabLabel().equals(label)) {
				return iTab;
			}
		}
		return null;
	}
	
}
