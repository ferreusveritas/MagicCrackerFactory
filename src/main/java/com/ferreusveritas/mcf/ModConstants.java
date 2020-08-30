package com.ferreusveritas.mcf;

public class ModConstants {
	
	public static final String MODID = "mcf";
	public static final String VERSION = "1.12.2-9999.9999.9999z";//Maxed out version to satisfy dependencies during dev, Assigned from gradle during build, do not change
	
	public static final String REQAFTER = "required-after:";
	
	//Other Mods
	public static final String COMPUTERCRAFT = "computercraft";
	
	public static final String DEPENDENCIES	= REQAFTER + COMPUTERCRAFT;
}
