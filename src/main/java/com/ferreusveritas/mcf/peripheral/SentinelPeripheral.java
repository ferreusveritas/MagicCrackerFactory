package com.ferreusveritas.mcf.peripheral;

import com.ferreusveritas.mcf.block.entity.SentinelBlockEntity;
import com.ferreusveritas.mcf.util.ZoneManager;
import com.ferreusveritas.mcf.util.bounds.StorageBounds;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.*;

import java.util.*;
import java.util.stream.Collectors;

public class SentinelPeripheral extends MCFPeripheral<SentinelBlockEntity> {

    public SentinelPeripheral(SentinelBlockEntity block) {
        super(block);
    }

    public static StorageBounds.BoundsType validateBoundsType(String boundsType) throws LuaException {
        StorageBounds.BoundsType type = StorageBounds.BoundsType.getType(boundsType);
        if (type == StorageBounds.BoundsType.EMPTY) {
            String valids = String.join(", ", StorageBounds.BoundsType.VALID.stream().map(t -> t.getLabel()).collect(Collectors.toList()));
            throw new LuaException("boundsType \"" + boundsType + "\" is not valid. Must be one of: " + valids);
        }
        return type;
    }

    public static ArrayList<Tuple<Vec3, Vec3>> getLineSegmentsFromLuaObject(Object obj) {
        ArrayList<Tuple<Vec3, Vec3>> lineSegments = new ArrayList<>();

        if (obj instanceof HashMap) {
            HashMap<?, ?> arrayMap = (HashMap<?, ?>) obj;

            arrayMap.forEach((k, subArray) -> {
                if (subArray instanceof HashMap) {
                    HashMap<?, ?> doubleMap = (HashMap<?, ?>) subArray;

                    if (doubleMap.size() == 6) {
                        double[] vecPrims = new double[6];

                        for (int i = 0; i < 6; i++) {
                            Object val = doubleMap.get((double) (i + 1));
                            vecPrims[i] = val instanceof Double ? (Double) val : 0.0;
                        }

                        Vec3 start = new Vec3(vecPrims[0], vecPrims[1], vecPrims[2]);
                        Vec3 end = new Vec3(vecPrims[3], vecPrims[4], vecPrims[5]);

                        lineSegments.add(new Tuple<>(start, end));
                    }
                }
            });
        }

        return lineSegments;
    }

    public static Object[] doRayTraceBatch(Level level, ArrayList<Tuple<Vec3, Vec3>> lineSegments, boolean checkEntities) {
        ArrayList<Object> results = new ArrayList<>();
        lineSegments.forEach(tuple -> results.add(doRayTrace(level, tuple.getA(), tuple.getB(), checkEntities)));
        return results.toArray();
    }

    public static Map<String, Object> doRayTrace(Level level, Vec3 start, Vec3 end, boolean checkEntities) {
        Map<String, Object> values = new HashMap<>();

        HitResult hit = level.clip(new ClipContext(start, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, null));
        BlockHitResult.Type hitType = (hit != null && hit.getType() == BlockHitResult.Type.BLOCK) ? BlockHitResult.Type.BLOCK : BlockHitResult.Type.MISS;

        if (hitType == BlockHitResult.Type.BLOCK) {
            end = hit.getLocation();
        }

        if (checkEntities) {
            AABB aabb = new AABB(start.x, start.y, start.z, end.x, end.y, end.z);
            List<Entity> entities = level.getEntitiesOfClass(Entity.class, aabb, entity -> true);

            double closestDistance = Double.POSITIVE_INFINITY;
            for (Entity e : entities) {
                Optional<Vec3> entityResult = e.getBoundingBox().clip(start, end);
                if (entityResult.isPresent()) {
                    hitType = BlockHitResult.Type.ENTITY;
                    double dist = start.distanceTo(entityResult.get());
                    if (dist < closestDistance) {
                        closestDistance = dist;
                        hit = new EntityHitResult(e, entityResult.get());
                    }
                }
            }
        }

        if (hit != null) {
            end = hit.getLocation();
            values.put("hitPos", new Object[]{end.x, end.y, end.z});
            if (hit instanceof BlockHitResult blockResult) {
                BlockPos blockPos = blockResult.getBlockPos();
                values.put("blockPos", new Object[]{blockPos.getX(), blockPos.getY(), blockPos.getZ()});
                values.put("side", (double) blockResult.getDirection().ordinal());
            } else {
                Entity entity = ((EntityHitResult) hit).getEntity();
                values.put("entity", entity.getName());
                values.put("class", entity.getClass().getSimpleName());
                values.put("id", entity.getId());
            }
        }

        values.put("type", hitType.name());

        return values;
    }

    @LuaFunction
    public final void addCuboidBounds(String name, String boundsType, int minX, int minY, int minZ, int maxX, int maxY, int maxZ) throws LuaException {
        StorageBounds.BoundsType type = validateBoundsType(boundsType);
        getZoneManager().addCuboidBounds(type, name, minX, minY, minZ, maxX, maxY, maxZ);
    }

    @LuaFunction
    public final void addCylinderBounds(String name, String boundsType, int x, int z, int minY, int maxY, int radius) throws LuaException {
        StorageBounds.BoundsType type = validateBoundsType(boundsType);
        getZoneManager().addCylinderBounds(type, name, x, z, minY, maxY, radius);
    }

    @LuaFunction
    public final void addAnyBounds(String name, String boundsType) throws LuaException {
        StorageBounds.BoundsType type = validateBoundsType(boundsType);
        getZoneManager().addAnyBounds(type, name);
    }

    @LuaFunction
    public final void remBounds(String name) throws LuaException {
        getZoneManager().remBounds(name);
    }

    @LuaFunction
    public final Object[] listBounds() {
        return getZoneManager().listBounds();
    }

    @LuaFunction
    public final void addEntityFilter(String boundsName, String filterName, String filterType, String filterData) throws LuaException {
        getZoneManager().addEntityFilter(boundsName, filterName, filterType, filterData);
    }

    @LuaFunction
    public final void remEntityFilter(String boundsName, String filterName) throws LuaException {
        getZoneManager().remEntityFilter(boundsName, filterName);
    }

    @LuaFunction
    public final Map<String, Object> getBoundsData(String name) throws LuaException {
        return getZoneManager().getBoundsDataLua(name);
    }

    @LuaFunction
    public final Object[] getEntitiesInBounds(String name) {
        return getZoneManager().getEntitiesInBounds(block.getLevel(), name, Entity.class);
    }

    @LuaFunction
    public final Object[] getPlayersInBounds(String name) {
        return getZoneManager().getPlayersInBounds(block.getLevel(), name);
    }

    @LuaFunction
    public final Map<String, Object> rayTrace(int startX, int startY, int startZ, int endX, int endY, int endZ, boolean checkEntities) {
        return doRayTrace(block.getLevel(), new Vec3(startX, startY, startX), new Vec3(endX, endY, endZ), checkEntities);
    }

    @LuaFunction
    public final Object[] rayTraceBatch(Object lineSegmentArray, boolean checkEntities) {
        ArrayList<Tuple<Vec3, Vec3>> lineSegments = getLineSegmentsFromLuaObject(lineSegmentArray);
        return doRayTraceBatch(block.getLevel(), lineSegments, checkEntities);
    }

    private ZoneManager getZoneManager() {
        return ZoneManager.get(((ServerLevel) block.getLevel()));
    }

}
