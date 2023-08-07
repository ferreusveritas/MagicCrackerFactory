package com.ferreusveritas.mcf.util;

import com.ferreusveritas.mcf.util.bounds.*;
import com.ferreusveritas.mcf.util.bounds.StorageBounds.BoundsType;
import com.ferreusveritas.mcf.util.filter.SetEntityFilter;
import dan200.computercraft.api.lua.LuaException;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.phys.AABB;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.*;
import java.util.Map.Entry;

public class ZoneManager extends SavedData {

    private static final Logger LOGGER = LogManager.getLogger();
    private final static String ID = "SecurityZones";
    protected StorageBounds storageBounds = new StorageBounds(new CompoundTag());

    public static ZoneManager get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(ZoneManager::load, ZoneManager::new, ID);
    }

    public static ZoneManager load(CompoundTag tag) {
        LOGGER.info("Reading tag from disk for ZoneManager");
        ZoneManager zoneManager = new ZoneManager();
        zoneManager.setBoundsStorage(new StorageBounds(tag.getCompound("zones")));
        return zoneManager;
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        tag.put("zones", storageBounds.toCompoundTag());
        return tag;
    }

    public StorageBounds getBoundsStorage() {
        return storageBounds;
    }

    public void setBoundsStorage(StorageBounds storage) {
        storageBounds = storage;
        setDirty();
    }

    //Additions


    public void addBounds(BoundsType type, String name, Bounds bb) throws LuaException {
        //System.out.println("Bounds Added: " + type + ", " + name + ", " + bb);
        if (boundsExists(name)) {
            throw new LuaException("Bounds name \"" + name + "\" already exists");
        }

        getBoundsStorage().getByType(type).put(name, bb);
        addDefaultFilters(type, bb);
        setDirty();
    }

    public void addDefaultFilters(BoundsType type, Bounds bb) {
        Optional.ofNullable(type.getDefaultEntityFilter()).ifPresent(filter -> bb.getFilterSet().setFilter("default", filter));
    }

    public void addCuboidBounds(BoundsType type, String name, int minX, int minY, int minZ, int maxX, int maxY, int maxZ) throws LuaException {
        addBounds(type, name, new CuboidBounds(Arrays.asList(new BlockPos(minX, minY, minZ), new BlockPos(maxX, maxY, maxZ))));
    }

    public void addCylinderBounds(BoundsType type, String name, int posX, int posZ, int minY, int maxY, int radius) throws LuaException {
        addBounds(type, name, new CylinderBounds(new BlockPos(posX, minY, posZ), maxY - minY, radius));
    }

    public void addAnyBounds(BoundsType type, String name) throws LuaException {
        addBounds(type, name, new AnyBounds());
    }

    public void addEntityFilter(String name, String filterName, String filterType, String filterData) throws LuaException {
        //System.out.println("Entity Filter Added: " + type + ", " + name + ", " + filterName + ", " +  filterType + ", " + filterData);
        Bounds bounds = checkBoundName(name);
        checkFilterType(filterType);

        bounds.getFilterSet().setFilter(filterName, filterType, filterData);
        setDirty();
    }

    public void remEntityFilter(String name, String filterName) throws LuaException {
        //System.out.println("Entity Filter Removed: " + type + ", " + name + ", " + filterName);
        Bounds bounds = checkBoundName(name);

        SetEntityFilter filterSet = getBounds(name).getFilterSet();

        if (filterSet.getFilter(filterName) == null) {
            String valids = String.join(", ", filterSet.getFilterNames());
            throw new LuaException("filter: \"" + filterName + "\" does not exist in this bounds. Available filters: " + valids);
        }

        bounds.getFilterSet().remFilter(filterName);
        setDirty();
    }

    //Removals

    public void remBounds(String name) throws LuaException {
        //System.out.println("Bounds Removed: " + name);
        checkBoundName(name);
        for (BoundsType type : BoundsType.values()) {
            getBoundsStorage().getByType(type).remove(name);
        }
        setDirty();
    }

    public Object[] listBounds() {
        List<Object> bounds = new ArrayList<>();

        for (BoundsType type : BoundsType.values()) {
            for (Entry<String, Bounds> bound : getBoundsStorage().getByType(type).entrySet()) {
                Map<String, Object> data = new HashMap<>();
                data.put("name", bound.getKey());
                data.put("type", type.getLabel());
                data.put("shape", bound.getValue().getBoundType());
                AABB aabb = bound.getValue().getAABB();
                if (aabb != null) {
                    data.put("aabb", new Object[]{new Double[]{aabb.minX, aabb.minY, aabb.minZ}, new Double[]{aabb.maxX, aabb.maxY, aabb.maxZ}});
                }
                data.put("filters", bound.getValue().getFilterSet().filtersToLuaObject());

                bounds.add(data);
            }
        }

        return bounds.toArray(new Object[0]);
    }

    public Map<String, Object> getBoundsDataLua(String name) throws LuaException {
        return checkBoundName(name).collectLuaData();
    }

    public Bounds checkBoundName(String name) throws LuaException {
        Bounds bound = getBounds(name);
        if (bound == null) {
            String valids = String.join(", ", getBoundsNames());
            throw new LuaException("Bounds name \"" + name + "\" does not exist. Must be one of: " + valids);
        }
        return bound;
    }

    public void checkFilterType(String filterType) throws LuaException {
        if (!SetEntityFilter.FILTER_PROVIDERS.containsKey(filterType)) {
            String valids = String.join(", ", SetEntityFilter.FILTER_PROVIDERS.keySet());
            throw new LuaException("filterType \"" + filterType + "\" is not valid. Must be one of: " + valids);
        }
    }

    public boolean boundsExists(String name) {
        return getBounds(name) != null;
    }

    public Bounds getBounds(String name) {

        for (BoundsType type : BoundsType.values()) {
            Bounds bounds = getBoundsStorage().getByType(type).get(name);
            if (bounds != null) {
                return bounds;
            }
        }

        return null;
    }

    public Set<String> getBoundsNames() {
        Set<String> allNames = new HashSet<>();

        for (BoundsType type : BoundsType.values()) {
            allNames.addAll(getBoundsStorage().getByType(type).keySet());
        }

        return allNames;
    }

    //Tests and Filters

    public boolean testBreakBounds(Player player, BlockPos pos) {
        return player != null && !player.isCreative() && testBreakBounds(pos);
    }

    public boolean testBreakBounds(BlockPos pos) {
        return testBounds(pos, getBoundsStorage().breakBounds);
    }

    public boolean testPlaceBounds(@Nullable Entity placer, BlockPos pos) {
        return placer != null && (!(placer instanceof Player) || !((Player) placer).isCreative()) && testPlaceBounds(pos);
    }

    public boolean testPlaceBounds(BlockPos pos) {
        return testBounds(pos, getBoundsStorage().placeBounds);
    }

    public boolean testBlastStart(BlockPos pos, LivingEntity entity) {
        return testBoundsAndEntity(pos, getBoundsStorage().blastBounds, entity);
    }

    public void filterBlastDetonate(List<BlockPos> blocks, LivingEntity entity) {
        getBoundsStorage().blastBounds.values().forEach(bb -> blocks.removeIf(p -> bb.inBounds(p) && bb.getFilterSet().isEntityDenied(entity)));
    }

    public boolean testSpawnBounds(BlockPos pos, LivingEntity entity) {
        return testBoundsAndEntity(pos, getBoundsStorage().spawnBounds, entity);
    }

    public boolean testEnderBounds(BlockPos pos, LivingEntity entity) {
        return testBoundsAndEntity(pos, getBoundsStorage().enderBounds, entity);
    }

    public boolean testBounds(BlockPos pos, Map<String, Bounds> bounds) {
        return bounds.values().parallelStream().anyMatch(bb -> bb.inBounds(pos));
    }

    public boolean testBoundsAndEntity(BlockPos pos, Map<String, Bounds> bounds, LivingEntity entity) {
        return bounds.values().parallelStream().filter(bb -> bb.inBounds(pos)).anyMatch(bb -> bb.getFilterSet().isEntityDenied(entity));
    }

    public Object[] getPlayersInBounds(Level level, String boundsName) {
        return getPlayersInBounds(level, getBoundsStorage().identBounds.get(boundsName));
    }

    public Object[] getPlayersInBounds(Level level, Bounds bb) {
        if (bb != null) {
            if (bb.getAABB() == null) {
                List<Entity> entities = new ArrayList<>(level.players());
                return getEntitiesAsObjects(entities);
            }
        }
        return getEntitiesInBounds(level, bb, Player.class);
    }

    public <E extends Entity> Object[] getEntitiesInBounds(Level level, String boundsName, Class<E> clazz) {
        return getEntitiesInBounds(level, getBoundsStorage().identBounds.get(boundsName), clazz);
    }

    public <E extends Entity> Object[] getEntitiesInBounds(Level level, Bounds bb, Class<E> clazz) {
        if (bb != null) {
            AABB aabb = bb.getAABB();
            if (aabb != null) {
                List<E> entities = level.getEntitiesOfClass(clazz, aabb);
                entities.removeIf(e -> !bb.inBounds(e.blockPosition()));
                return getEntitiesAsObjects(entities);
            }
        }

        return new Object[0];
    }

    public <E extends Entity> Object[] getEntitiesAsObjects(List<E> entities) {
        List<Map<String, Object>> allEntityData = new ArrayList<>();

        for (E e : entities) {
            Map<String, Object> entityData = new HashMap<>();
            BlockPos blockpos = e.blockPosition();
            entityData.put("name", e.getName());
            entityData.put("pos", new Object[]{e.getX(), e.getY(), e.getZ()});
            entityData.put("blockpos", new Object[]{blockpos.getX(), blockpos.getY(), blockpos.getZ()});
            entityData.put("id", e.getId());

            allEntityData.add(entityData);
        }

        return allEntityData.toArray(new Object[0]);
    }

}
