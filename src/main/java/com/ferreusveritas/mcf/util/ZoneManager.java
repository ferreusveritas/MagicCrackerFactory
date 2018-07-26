package com.ferreusveritas.mcf.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ferreusveritas.mcf.util.BoundsStorage.EnumBoundsType;
import com.ferreusveritas.mcf.util.bounds.AnyBounds;
import com.ferreusveritas.mcf.util.bounds.BaseBounds;
import com.ferreusveritas.mcf.util.bounds.CuboidBounds;
import com.ferreusveritas.mcf.util.bounds.CylinderBounds;
import com.ferreusveritas.mcf.util.bounds.FilterBounds;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

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
		addBounds(type, name, new CylinderBounds(new BlockPos(posX, minY, posZ), maxY - minY, radius));
	}
	
	public void addAnyBounds(EnumBoundsType type, String name) {
		addBounds(type, name, new AnyBounds());
	}
	
	public void addEntityFilter(World world, EnumBoundsType type, String name, String entity) {
		EntityEntry ee = ForgeRegistries.ENTITIES.getValue(new ResourceLocation(entity));
		BaseBounds origBounds = getBounds(type, name);
		if(origBounds instanceof FilterBounds) {
			origBounds = ((FilterBounds) origBounds).baseBounds;
		}
		addBounds(type, name, new FilterBounds(origBounds, ee));//Wrap the bounds object in a filter
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
		BaseBounds bound = getBounds(type, name);
		if(bound != null) {
			return bound.toLuaObject();
		}
		
		return new Object[0];
	}
	
	public BaseBounds getBounds(EnumBoundsType type, String name) {
		return getBoundsStorage().getByType(type).get(name);
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
	
	public boolean testSpawnBounds(BlockPos pos, EntityLivingBase entity) {
		return getBoundsStorage().spawnBounds.values().parallelStream().filter(bb -> bb.inBounds(pos)).anyMatch(bb -> testSpawnBounds(bb, entity));
	}
	
	public boolean testSpawnBounds(BaseBounds bounds, EntityLivingBase entity) {
		if(bounds instanceof FilterBounds) {
			return ((FilterBounds) bounds).isEntityDenied(entity);
		} else {
			return isMobHostile(entity);
		}
	}
	
	public static boolean isMobHostile(EntityLivingBase entity) {
		return entity instanceof EntityMob || entity instanceof EntitySlime;
	}
	
	public boolean testEnderBounds(BlockPos pos) {
		return boundsTest(pos, getBoundsStorage().enderBounds);
	}
	
	public boolean testSeedsBounds(BlockPos pos) {
		return boundsTest(pos, getBoundsStorage().seedsBounds);
	}
	
	public boolean boundsTest(BlockPos pos, Map<String, BaseBounds> bounds) {
		return bounds.values().parallelStream().anyMatch(bb -> bb.inBounds(pos));
	}
	
	public Object[] getPlayersInBounds(World world, String boundName) {
		
		BaseBounds bb = getBoundsStorage().identBounds.get(boundName);
		
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
