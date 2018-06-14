package com.ferreusveritas.mcf.util;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;

public class MyWorldData extends WorldSavedData {
	
	final static String key = "my.unique.string";
	
	public MyWorldData(String name) {
		super(name);
	}
	
	// Fields containing your data here
	
	public static MyWorldData forWorld(World world) {
		// Retrieves the MyWorldData instance for the given world, creating it if necessary
		MapStorage storage = world.getPerWorldStorage();
		MyWorldData result = (MyWorldData)storage.getOrLoadData(MyWorldData.class, key);
		if (result == null) {
			result = new MyWorldData("test");
			storage.setData(key, result);
		}
		return result;
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		// Get your data from the nbt here
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		
		nbt.toString();
		
		return null;
		// Put your data in the nbt here
	}
	
}