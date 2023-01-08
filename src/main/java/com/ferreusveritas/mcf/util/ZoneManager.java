package com.ferreusveritas.mcf.util;

import com.ferreusveritas.mcf.util.bounds.*;
import com.ferreusveritas.mcf.util.bounds.StorageBounds.BoundsType;
import com.ferreusveritas.mcf.util.filter.SetEntityFilter;
import dan200.computercraft.api.lua.LuaException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.WorldSavedData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.*;
import java.util.Map.Entry;

public class ZoneManager extends WorldSavedData {

    private static final Logger LOGGER = LogManager.getLogger();
    private final static String ID = "SecurityZones";
    protected StorageBounds storageBounds = new StorageBounds(new CompoundNBT());

    public ZoneManager(String id) {
        super(id);
    }

    public static ZoneManager get(ServerWorld world) {
        return world.getDataStorage().computeIfAbsent(() -> new ZoneManager(ID), ID);
    }

    @Override
    public void load(CompoundNBT tag) {
        LOGGER.info("Reading tag from disk for ZoneManager");
        storageBounds = new StorageBounds(tag.getCompound("zones"));
    }

    @Override
    public CompoundNBT save(CompoundNBT tag) {
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
                AxisAlignedBB aabb = bound.getValue().getAABB();
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
        if (!SetEntityFilter.filterProviders.containsKey(filterType)) {
            String valids = String.join(", ", SetEntityFilter.filterProviders.keySet());
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

    public boolean testBreakBounds(PlayerEntity player, BlockPos pos) {
        return player != null && !player.isCreative() && testBreakBounds(pos);
    }

    public boolean testBreakBounds(BlockPos pos) {
        return testBounds(pos, getBoundsStorage().breakBounds);
    }

    public boolean testPlaceBounds(@Nullable Entity placer, BlockPos pos) {
        return placer != null && (!(placer instanceof PlayerEntity) || !((PlayerEntity) placer).isCreative()) && testPlaceBounds(pos);
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

    public Object[] getPlayersInBounds(World world, String boundsName) {
        return getPlayersInBounds(world, getBoundsStorage().identBounds.get(boundsName));
    }

    public Object[] getPlayersInBounds(World world, Bounds bb) {
        if (bb != null) {
            if (bb.getAABB() == null) {
                List<Entity> entities = new ArrayList<>(world.players());
                return getEntitiesAsObjects(entities);
            }
        }
        return getEntitiesInBounds(world, bb, PlayerEntity.class);
    }

    public Object[] getEntitiesInBounds(World world, String boundsName, Class<? extends Entity> clazz) {
        return getEntitiesInBounds(world, getBoundsStorage().identBounds.get(boundsName), clazz);
    }

    public Object[] getEntitiesInBounds(World world, Bounds bb, Class<? extends Entity> clazz) {
        if (bb != null) {
            AxisAlignedBB aabb = bb.getAABB();
            if (aabb != null) {
                List<Entity> entities = world.getEntitiesOfClass(clazz, aabb);
                entities.removeIf(e -> !bb.inBounds(e.blockPosition()));
                return getEntitiesAsObjects(entities);
            }
        }

        return new Object[0];
    }

    public Object[] getEntitiesAsObjects(List<Entity> entities) {
        List<Map<String, Object>> allEntityData = new ArrayList<>();

        for (Entity e : entities) {
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
