package com.ferreusveritas.mcf.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ferreusveritas.mcf.util.bounds.BoundsAny;
import com.ferreusveritas.mcf.util.bounds.BoundsBase;
import com.ferreusveritas.mcf.util.bounds.BoundsCuboid;
import com.ferreusveritas.mcf.util.bounds.BoundsCylinder;
import com.ferreusveritas.mcf.util.bounds.BoundsStorage;
import com.ferreusveritas.mcf.util.bounds.BoundsStorage.EnumBoundsType;
import com.ferreusveritas.mcf.util.filters.IEntityFilter;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;

public class ZoneManager extends WorldSavedData {
	//I would just use an array for this but Mojang allows for negative dimension numbers so I don't know where I'd start.
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
	
	public void addBounds(EnumBoundsType type, String name, BoundsBase bb) {
		getBoundsStorage().getByType(type).put(name, bb);
		addDefaultFilters(type, bb);
		markDirty();
	}
	
	public void addDefaultFilters(EnumBoundsType type, BoundsBase bb) {
		IEntityFilter defaultFilter = type.getDefaultEntityFilter();
		if(defaultFilter != null) {
			bb.getFilterSet().setFilter("default", defaultFilter);
		}
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
		BoundsBase bb = getBounds(type, name);
		if(bb != null) {
			bb.getFilterSet().setFilter(filterName, filterType, filterData);
		}
	}
	
	public void remEntityFilter(EnumBoundsType type, String name, String filterName) {
		BoundsBase bb = getBounds(type, name);
		if(bb != null) {
			bb.getFilterSet().remFilter(filterName);
		}
	}
	
	//Removals
	
	public void remBounds(EnumBoundsType type, String name) {
		getBoundsStorage().getByType(type).remove(name);
		markDirty();
	}
	
	public String[] listBounds(EnumBoundsType type) {
		return getBoundsStorage().getByType(type).keySet().toArray(new String[0]);
	}
	
	public Object[] getBoundsDataLua(EnumBoundsType type, String name) {
		BoundsBase bound = getBounds(type, name);
		if(bound != null) {
			return bound.toLuaObject();
		}
		
		return new Object[0];
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
				singlePlayerData.put("pos", new Object[] { p.posX, p.posY, p.posZ } );
				singlePlayerData.put("blockpos", new Object[] { p.getPosition().getX(), p.getPosition().getY(), p.getPosition().getZ() } );
				allPlayerData.add(singlePlayerData);
			}
		}
		
		return allPlayerData.toArray(new Object[0]);
	}
	
}
