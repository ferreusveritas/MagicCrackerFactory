package com.ferreusveritas.mcf.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ferreusveritas.mcf.util.BoundsStorage.EnumBoundsType;
import com.ferreusveritas.mcf.util.bounds.BaseBounds;
import com.ferreusveritas.mcf.util.bounds.CuboidBounds;
import com.ferreusveritas.mcf.util.bounds.CylinderBounds;

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
	
	public ZoneManager(String name) {
		super(name);
	}
	
	public static ZoneManager forWorld(World world) {
		MapStorage storage = world.getPerWorldStorage();
		ZoneManager result = (ZoneManager)storage.getOrLoadData(ZoneManager.class, key);
		if (result == null) {
			result = new ZoneManager(key);
			storage.setData(key, result);
		}
		
		return result;
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		boundsStorage = new BoundsStorage(nbt.getCompoundTag("zones"));
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
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
	
	public void addBounds(EnumBoundsType type, String name, BaseBounds bb) {
		getBoundsStorage().getByType(type).put(name, bb);
		markDirty();
	}
	
	public void addCuboidBounds(EnumBoundsType type, String name, int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
		addBounds(type, name, new CuboidBounds(Arrays.asList(new BlockPos(minX, minY, minZ), new BlockPos(maxX, maxY, maxZ))));
	}
	
	public void addCylinderBounds(EnumBoundsType type, String name, int posX, int posZ, int minY, int maxY, int radius) {
		getBoundsStorage().getByType(type).put(name, new CylinderBounds(new BlockPos(posX, minY, posZ), maxY - minY, radius));
		markDirty();
	}
	
	//Removals
	
	public void remBounds(EnumBoundsType type, String name) {
		getBoundsStorage().getByType(type).remove(name);
		markDirty();
	}
	
	public String[] listBounds(EnumBoundsType type) {
		return getBoundsStorage().getByType(type).keySet().toArray(new String[0]);
	}
	
	public Object[] getBoundsData(EnumBoundsType type, String name) {
		BaseBounds bound = getBoundsStorage().getByType(type).get(name);
		if(bound != null) {
			return bound.toLuaObject();
		}
		
		return new Object[0];
	}
	
	//Tests and Filters
	
	public boolean testBreakBounds(EntityPlayer player, BlockPos pos) {
		return player != null && !player.isCreative() && boundsTest(pos, getBoundsStorage().breakBounds);
	}
	
	public boolean testPlaceBounds(EntityPlayer player, BlockPos pos) {
		return player != null && !player.isCreative() && boundsTest(pos, getBoundsStorage().placeBounds);
	}
	
	public boolean testBlastStart(BlockPos pos) {
		return boundsTest(pos, getBoundsStorage().blastBounds);
	}
	
	public void filterBlastDetonate(List<BlockPos> blocks) {
		getBoundsStorage().blastBounds.values().forEach(bb -> blocks.removeIf(p -> bb.inBounds(p)));
	}
	
	public boolean testSpawnBounds(BlockPos pos) {
		return boundsTest(pos, getBoundsStorage().spawnBounds);
	}
	
	public boolean testEnderBounds(BlockPos pos) {
		return boundsTest(pos, getBoundsStorage().enderBounds);
	}
	
	public boolean boundsTest(BlockPos pos, Map<String, BaseBounds> bounds) {
		return bounds.values().parallelStream().anyMatch(bb -> bb.inBounds(pos));
	}
	
}
