package com.ferreusveritas.mcf;

public class ModConstants extends com.ferreusveritas.dynamictrees.ModConstants {
	
	public static final String MODID = "mcf";
	public static final String VERSION = "1.12.2-9999.9999.9999z";//Maxed out version to satisfy dependencies during dev, Assigned from gradle during build, do not change
	
	//Other Mods
	public static final String COMPUTERCRAFT = "computercraft";
	
	public static final String DEPENDENCIES 
			= REQAFTER + COMPUTERCRAFT
			+ NEXT
			+ REQAFTER + com.ferreusveritas.dynamictrees.ModConstants.MODID;
}
