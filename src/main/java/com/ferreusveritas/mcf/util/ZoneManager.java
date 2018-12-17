package com.ferreusveritas.mcf.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.ferreusveritas.mcf.util.bounds.BoundsAny;
import com.ferreusveritas.mcf.util.bounds.BoundsBase;
import com.ferreusveritas.mcf.util.bounds.BoundsCuboid;
import com.ferreusveritas.mcf.util.bounds.BoundsCylinder;
import com.ferreusveritas.mcf.util.bounds.BoundsStorage;
import com.ferreusveritas.mcf.util.bounds.BoundsStorage.EnumBoundsType;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;

public class ZoneManager extends WorldSavedData {
	
	final static String key = "SecurityZones";
	
	public static ZoneManager get(World world) {
		MapStorage storage = world.getPerWorldStorage();
		return Optional.ofNullable((ZoneManager)storage.getOrLoadData(ZoneManager.class, key)).orElseGet( () -> { 
			ZoneManager result = new ZoneManager(key);
			storage.setData(key, result);
			return result;
		});
	}
	
	protected BoundsStorage boundsStorage = new BoundsStorage(new NBTTagCompound());
	
	public ZoneManager(String name) {
		super(name);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		System.out.println("Info: Reading NBT data from disk for ZoneManager");
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
	
	public void addBounds(EnumBoundsType type, String name, BoundsBase bb) {
		//System.out.println("Bounds Added: " + type + ", " + name + ", " + bb);
		getBoundsStorage().getByType(type).put(name, bb);
		addDefaultFilters(type, bb);
		markDirty();
	}
	
	public void addDefaultFilters(EnumBoundsType type, BoundsBase bb) {
		Optional.ofNullable(type.getDefaultEntityFilter()).ifPresent(filter -> bb.getFilterSet().setFilter("default", filter));
	}
	
	public void addCuboidBounds(EnumBoundsType type, String name, int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
		addBounds(type, name, new BoundsCuboid(Arrays.asList(new BlockPos(minX, minY, minZ), new BlockPos(maxX, maxY, maxZ))));
	}
	
	public void addCylinderBounds(EnumBoundsType type, String name, int posX, int posZ, int minY, int maxY, int radius) {
		addBounds(type, name, new BoundsCylinder(new BlockPos(posX, minY, posZ), maxY - minY, radius));
	}
	
	public void addAnyBounds(EnumBoundsType type, String name) {
		addBounds(type, name, new BoundsAny());
	}
	
	public void addEntityFilter(EnumBoundsType type, String name, String filterName, String filterType, String filterData) {
		//System.out.println("Entity Filter Added: " + type + ", " + name + ", " + filterName + ", " +  filterType + ", " + filterData);
		Optional.ofNullable(getBounds(type, name)).ifPresent(bb -> bb.getFilterSet().setFilter(filterName, filterType, filterData));
		markDirty();
	}
	
	public void remEntityFilter(EnumBoundsType type, String name, String filterName) {
		//System.out.println("Entity Filter Removed: " + type + ", " + name + ", " + filterName);
		Optional.ofNullable(getBounds(type, name)).ifPresent(bb -> bb.getFilterSet().remFilter(filterName));
		markDirty();
	}
	
	//Removals
	
	public void remBounds(EnumBoundsType type, String name) {
		//System.out.println("Bounds Removed: " + type + ", " + name);
		getBoundsStorage().getByType(type).remove(name);
		markDirty();
	}
	
	public String[] listBounds(EnumBoundsType type) {
		return getBoundsStorage().getByType(type).keySet().toArray(new String[0]);
	}
	
	public Object[] getBoundsDataLua(EnumBoundsType type, String name) {
		return Optional.ofNullable(getBounds(type, name)).map(b -> b.toLuaObject()).orElse(new Object[0]);
	}
	
	public BoundsBase getBounds(EnumBoundsType type, String name) {
		return getBoundsStorage().getByType(type).get(name);
	}
	
	//Tests and Filters
	
	public boolean testBreakBounds(EntityPlayer player, BlockPos pos) {
		return player != null && !player.isCreative() && testBounds(pos, getBoundsStorage().breakBounds);
	}
	
	public boolean testPlaceBounds(EntityPlayer player, BlockPos pos) {
		return player != null && !player.isCreative() && testBounds(pos, getBoundsStorage().placeBounds);
	}
	
	public boolean testBlastStart(BlockPos pos, EntityLivingBase entity) {
		return testBoundsAndEntity(pos, getBoundsStorage().blastBounds, entity);
	}
	
	public void filterBlastDetonate(List<BlockPos> blocks, EntityLivingBase entity) {
		getBoundsStorage().blastBounds.values().forEach(bb -> blocks.removeIf(p -> bb.inBounds(p) && bb.getFilterSet().isEntityDenied(entity)));
	}
	
	public boolean testSpawnBounds(BlockPos pos, EntityLivingBase entity) {
		return testBoundsAndEntity(pos, getBoundsStorage().spawnBounds, entity);
	}
	
	public boolean testEnderBounds(BlockPos pos, EntityLivingBase living) {
		return testBoundsAndEntity(pos, getBoundsStorage().enderBounds, living);
	}
	
	public boolean testSeedsBounds(BlockPos pos) {
		return testBounds(pos, getBoundsStorage().seedsBounds);
	}
	
	public boolean testBounds(BlockPos pos, Map<String, BoundsBase> bounds) {
		return bounds.values().parallelStream().anyMatch(bb -> bb.inBounds(pos));
	}
	
	public boolean testBoundsAndEntity(BlockPos pos, Map<String, BoundsBase> bounds, EntityLivingBase entity) {
		return bounds.values().parallelStream().filter(bb -> bb.inBounds(pos)).anyMatch(bb -> bb.getFilterSet().isEntityDenied(entity));
	}
	
	public Object[] getPlayersInBounds(World world, String boundName) {
		
		BoundsBase bb = getBoundsStorage().identBounds.get(boundName);
		
		AxisAlignedBB aabb = bb.getAABB();
		List<EntityPlayer> players = aabb != null ? world.getEntitiesWithinAABB(EntityPlayer.class, aabb) : world.playerEntities;
		List<Map<String, Object>> allPlayerData = new ArrayList<>();
		
		for(EntityPlayer p : players) {
			if(bb.inBounds(p.getPosition())) {
				Map<String, Object> singlePlayerData = new HashMap<>();
				singlePlayerData.put("name", p.getName());

				Map<String, Double> posMap = new HashMap<>();
				posMap.put("x", p.posX);
				posMap.put("y", p.posY);
				posMap.put("z", p.posZ);
				singlePlayerData.put("pos", posMap);

				Map<String, Integer> blockPosMap = new HashMap<>();
				BlockPos blockpos = p.getPosition();
				blockPosMap.put("x", blockpos.getX());
				blockPosMap.put("y", blockpos.getY());
				blockPosMap.put("z", blockpos.getZ());
				singlePlayerData.put("blockpos", blockPosMap);

				allPlayerData.add(singlePlayerData);
			}
		}
		
		return allPlayerData.toArray(new Object[0]);
	}
	
}
