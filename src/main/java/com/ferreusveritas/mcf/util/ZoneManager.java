package com.ferreusveritas.mcf.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.ferreusveritas.mcf.util.BoundsStorage.EnumBoundsType;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;

public class ZoneManager extends WorldSavedData {
	public static HashMap<Integer, ZoneManager> zoneManagers = new HashMap<>();
	
	final static String key = "SecurityZones";
	
	public static ZoneManager getZoneManager(World world) {
		return zoneManagers.computeIfAbsent( world.provider.getDimension(), key -> forWorld(world));
	}
	
	protected BoundsStorage boundsStorage = new BoundsStorage(new NBTTagCompound());
	
	public ZoneManager(String name, World world) {
		super(name);
	}
	
	public static ZoneManager forWorld(World world) {
		MapStorage storage = world.getPerWorldStorage();
		ZoneManager result = (ZoneManager)storage.getOrLoadData(ZoneManager.class, key);
		if (result == null) {
			result = new ZoneManager(key, world);
			storage.setData(key, result);
		}
		
		return result;
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX READING XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
		boundsStorage = new BoundsStorage(nbt.getCompoundTag("zones"));
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX WRITING XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
		nbt.setTag("zones", boundsStorage.toNBTTagCompound());
		return nbt;
	}
	
	public BoundsStorage getBoundsStorage() {
		return boundsStorage;
	}
	
	public void setBoundsStorage(BoundsStorage storage) {
		boundsStorage = storage;
		markDirty();
	}
	
	//Additions
	
	public void addBounds(EnumBoundsType type, String name, Bounds bb) {
		getBoundsStorage().getByType(type).put(name, bb);
		markDirty();
	}
	
	public void addBounds(EnumBoundsType type, String name, int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
		addBounds(type, name, new BlockBounds(Arrays.asList(new BlockPos(minX, minY, minZ), new BlockPos(maxX, maxY, maxZ))));
	}
	
	//Removals
	
	public void remBounds(EnumBoundsType type, String name) {
		getBoundsStorage().getByType(type).remove(name);
		markDirty();
	}
	
	public String[] listBounds(EnumBoundsType type) {
		return getBoundsStorage().getByType(type).keySet().toArray(new String[0]);
	}
	
	//Tests
	
	public boolean testBreakBounds(EntityPlayer player, BlockPos pos) {
		return player != null && !player.isCreative() && getBoundsStorage().breakBounds.values().parallelStream().anyMatch(bb -> bb.inBounds(pos));
	}
	
	public boolean testPlaceBounds(EntityPlayer player, BlockPos pos) {
		return player != null && !player.isCreative() && getBoundsStorage().placeBounds.values().parallelStream().anyMatch(bb -> bb.inBounds(pos));
	}
	
	public boolean testBlastStart(BlockPos pos) {
		return getBoundsStorage().blastBounds.values().parallelStream().anyMatch(bb -> bb.inBounds(pos));
	}
	
	public void filterBlastDetonate(List<BlockPos> blocks) {
		getBoundsStorage().blastBounds.values().forEach(bb -> blocks.removeIf(p -> bb.inBounds(p)));
	}
	
	public boolean testSpawnBounds(BlockPos pos) {
		return getBoundsStorage().spawnBounds.values().parallelStream().anyMatch(bb -> bb.inBounds(pos));
	}
	
}
