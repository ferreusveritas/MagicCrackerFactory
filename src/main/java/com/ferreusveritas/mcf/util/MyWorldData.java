package com.ferreusveritas.mcf.util;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;

public class MyWorldData extends WorldSavedData {
	
	final static String key = "SecurityZones";
	
	public String m = "";
	
	public MyWorldData(String name) {
		super(name);
	}
	
	// Fields containing your data here
	
	public static MyWorldData forWorld(World world) {
		// Retrieves the MyWorldData instance for the given world, creating it if necessary
		
		MapStorage storage = world.getPerWorldStorage();
		MyWorldData result = (MyWorldData)storage.getOrLoadData(MyWorldData.class, key);
		if (result == null) {
			result = new MyWorldData(key);
			storage.setData(key, result);
		}
		
		return result;
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX READING XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
		m = nbt.getString("Malcom");
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX WRITING XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
		nbt.setString("Malcom", m);
		return nbt;
	}
	
}