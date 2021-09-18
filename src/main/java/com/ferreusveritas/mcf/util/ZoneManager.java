package com.ferreusveritas.mcf.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;

import com.ferreusveritas.mcf.util.bounds.BoundsAny;
import com.ferreusveritas.mcf.util.bounds.BoundsBase;
import com.ferreusveritas.mcf.util.bounds.BoundsCuboid;
import com.ferreusveritas.mcf.util.bounds.BoundsCylinder;
import com.ferreusveritas.mcf.util.bounds.BoundsStorage;
import com.ferreusveritas.mcf.util.bounds.BoundsStorage.EnumBoundsType;
import com.ferreusveritas.mcf.util.filters.EntityFilterSet;

import dan200.computercraft.api.lua.LuaException;
import net.minecraft.entity.Entity;
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
	
	public void addBounds(EnumBoundsType type, String name, BoundsBase bb) throws LuaException {
		//System.out.println("Bounds Added: " + type + ", " + name + ", " + bb);
		if(boundsExists(name)) {
			throw new LuaException("Bounds name \"" + name + "\" already exists");
		}
		
		getBoundsStorage().getByType(type).put(name, bb);
		addDefaultFilters(type, bb);
		markDirty();
	}
	
	public void addDefaultFilters(EnumBoundsType type, BoundsBase bb) {
		Optional.ofNullable(type.getDefaultEntityFilter()).ifPresent(filter -> bb.getFilterSet().setFilter("default", filter));
	}
	
	public void addCuboidBounds(EnumBoundsType type, String name, int minX, int minY, int minZ, int maxX, int maxY, int maxZ) throws LuaException {
		addBounds(type, name, new BoundsCuboid(Arrays.asList(new BlockPos(minX, minY, minZ), new BlockPos(maxX, maxY, maxZ))));
	}
	
	public void addCylinderBounds(EnumBoundsType type, String name, int posX, int posZ, int minY, int maxY, int radius) throws LuaException {
		addBounds(type, name, new BoundsCylinder(new BlockPos(posX, minY, posZ), maxY - minY, radius));
	}
	
	public void addAnyBounds(EnumBoundsType type, String name) throws LuaException {
		addBounds(type, name, new BoundsAny());
	}
	
	public void addEntityFilter(String name, String filterName, String filterType, String filterData) throws LuaException {
		//System.out.println("Entity Filter Added: " + type + ", " + name + ", " + filterName + ", " +  filterType + ", " + filterData);
		BoundsBase bounds = checkBoundName(name);
		checkFilterType(filterType);
		
		bounds.getFilterSet().setFilter(filterName, filterType, filterData);
		markDirty();
	}
	
	public void remEntityFilter(String name, String filterName) throws LuaException {
		//System.out.println("Entity Filter Removed: " + type + ", " + name + ", " + filterName);
		BoundsBase bounds = checkBoundName(name);
		
		EntityFilterSet filterSet = getBounds(name).getFilterSet();
		
		if(filterSet.getFilter(filterName) == null) {
			String valids = String.join(", ", filterSet.getFilterNames());
			throw new LuaException("filter: \"" + filterName + "\" does not exist in this bounds. Available filters: " + valids);
		}
		
		bounds.getFilterSet().remFilter(filterName);
		markDirty();
	}
	
	//Removals
	
	public void remBounds(String name) throws LuaException {
		//System.out.println("Bounds Removed: " + name);
		checkBoundName(name);
		for(EnumBoundsType type : EnumBoundsType.values()) {
			getBoundsStorage().getByType(type).remove(name);
		}
		markDirty();
	}
	
	public Object[] listBounds() {
		List<Object> bounds = new ArrayList<>();
		
		for(EnumBoundsType type : EnumBoundsType.values()) {
			for(Entry<String, BoundsBase> bound : getBoundsStorage().getByType(type).entrySet()) {
				Map<String, Object> data = new HashMap<>();
				data.put("name", bound.getKey());
				data.put("type", type.getLabel());
				data.put("shape", bound.getValue().getBoundType());
				AxisAlignedBB aabb = bound.getValue().getAABB();
				if(aabb != null) {
					data.put("aabb", new Object[] { new Double[] { aabb.minX, aabb.minY, aabb.minZ }, new Double[] { aabb.maxX, aabb.maxY, aabb.maxZ } } );
				}
				data.put("filters", bound.getValue().getFilterSet().filtersToLuaObject());
				
				bounds.add(data);
			}
		}
		
		return bounds.toArray(new Object[0]);
	}
	
	public Object[] getBoundsDataLua(String name) throws LuaException {
		return checkBoundName(name).toLuaObject();
	}
	
	public BoundsBase checkBoundName(String name) throws LuaException {
		BoundsBase bound = getBounds(name);
		if(bound == null) {
			String valids = String.join(", ", getBoundsNames());
			throw new LuaException("Bounds name \"" + name + "\" does not exist. Must be one of: " + valids);
		}
		return bound;
	}
	
	public void checkFilterType(String filterType) throws LuaException {
		if(!EntityFilterSet.filterProviders.keySet().contains(filterType)) {
			String valids = String.join(", ", EntityFilterSet.filterProviders.keySet());
			throw new LuaException("filterType \"" + filterType + "\" is not valid. Must be one of: " + valids );
		}
	}
	
	public boolean boundsExists(String name) {
		return getBounds(name) != null;
	}
	
	public BoundsBase getBounds(String name) {
		
		for(EnumBoundsType type : EnumBoundsType.values()) {
			BoundsBase bounds = getBoundsStorage().getByType(type).get(name);
			if(bounds != null) {
				return bounds;
			}
		}
		
		return null;
	}
	
	public Set<String> getBoundsNames() {
		Set<String> allNames = new HashSet<>();
		
		for(EnumBoundsType type : EnumBoundsType.values()) {
			allNames.addAll(getBoundsStorage().getByType(type).keySet()); 
		}
		
		return allNames;
	}
	
	//Tests and Filters
	
	public boolean testBreakBounds(EntityPlayer player, BlockPos pos) {
		return player != null && !player.isCreative() && testBreakBounds(pos);
	}
	
	public boolean testBreakBounds(BlockPos pos) {
		return testBounds(pos, getBoundsStorage().breakBounds);
	}
	
	public boolean testPlaceBounds(EntityPlayer player, BlockPos pos) {
		return player != null && !player.isCreative() && testPlaceBounds(pos);
	}
	
	public boolean testPlaceBounds(BlockPos pos) {
		return testBounds(pos, getBoundsStorage().placeBounds);
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
	
	public Object[] getPlayersInBounds(World world, String boundsName) {
		return getPlayersInBounds(world, getBoundsStorage().identBounds.get(boundsName));
	}
	
	public Object[] getPlayersInBounds(World world, BoundsBase bb) {
		if(bb != null) {
			if(bb.getAABB() == null) {
				List<Entity> entities = new ArrayList<>();
				world.playerEntities.forEach(e -> entities.add(e));
				return getEntitiesAsObjects(entities);
			}
		}
		return getEntitiesInBounds(world, bb, EntityPlayer.class);
	}
	
	public Object[] getEntitiesInBounds(World world, String boundsName, Class clazz) {
		return getEntitiesInBounds(world, getBoundsStorage().identBounds.get(boundsName), clazz);
	}
	
	public Object[] getEntitiesInBounds(World world, BoundsBase bb, Class clazz) {
		if(bb != null) {
			AxisAlignedBB aabb = bb.getAABB();
			if(aabb != null) {
				List<Entity> entities = world.getEntitiesWithinAABB(clazz, aabb);
				entities.removeIf(e -> !bb.inBounds(e.getPosition()));
				return getEntitiesAsObjects(entities);
			}
		}
		
		return new Object[0];
	}
	
	public Object[] getEntitiesAsObjects(List<Entity> entities) {
		List<Map<String, Object>> allEntityData = new ArrayList<>();
		
		for(Entity e : entities) {
			Map<String, Object> entityData = new HashMap<>();
			BlockPos blockpos = e.getPosition();
			entityData.put("name", e.getName());
			entityData.put("pos", new Object[] { e.posX, e.posY, e.posZ } );
			entityData.put("blockpos", new Object[] { blockpos.getX(), blockpos.getY(), blockpos.getZ() } );
			entityData.put("id", e.getEntityId());
			
			allEntityData.add(entityData);
		}
		
		return allEntityData.toArray(new Object[0]);
	}
	
}
