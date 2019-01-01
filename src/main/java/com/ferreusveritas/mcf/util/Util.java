package com.ferreusveritas.mcf.util;

import java.lang.reflect.Field;

public class Util {
	
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
	
	public static void setRestrictedObject(Class clazz, Object from, Object value, String ... objNames) {
		for(String objName: objNames) {
			try {
				Field field = clazz.getDeclaredField(objName);
				field.setAccessible(true);
				field.set(from, value);
			} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) { }
		}
		
	}
	
}
