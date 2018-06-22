package com.ferreusveritas.mcf.util;

import com.ferreusveritas.mcf.MCF;

import net.minecraft.creativetab.CreativeTabs;

public class Util {
	
	public static CreativeTabs findCreativeTab(String label) {
		return MCF.proxy.findCreativeTab(label);
	}
	
}
