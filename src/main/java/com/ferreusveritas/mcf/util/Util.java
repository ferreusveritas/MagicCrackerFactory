package com.ferreusveritas.mcf.util;

import java.lang.reflect.Field;

import com.ferreusveritas.mcf.MCF;

import net.minecraft.creativetab.CreativeTabs;

public class Util {
	
	public static CreativeTabs findCreativeTab(String label) {
		return MCF.proxy.findCreativeTab(label);
	}
	
	public static Object getRestrictedObject(Class clazz, Object from, String ... objNames) {
		for(String objName: objNames) {
			try {
				Field field = clazz.getDeclaredField(objName);
				field.setAccessible(true);
				return field.get(from);
			} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) { }
		}
		
		return null;
	}
	
}
