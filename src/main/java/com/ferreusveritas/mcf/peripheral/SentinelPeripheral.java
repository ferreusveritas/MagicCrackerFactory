package com.ferreusveritas.mcf.peripheral;

import com.ferreusveritas.mcf.tileentity.SentinelTileEntity;
import com.ferreusveritas.mcf.util.ZoneManager;
import com.ferreusveritas.mcf.util.bounds.StorageBounds;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import net.minecraft.entity.Entity;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.*;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.*;
import java.util.stream.Collectors;

public class SentinelPeripheral extends MCFPeripheral<SentinelTileEntity> {

    public SentinelPeripheral(SentinelTileEntity block) {
        super(block);
    }

    public static StorageBounds.BoundsType validateBoundsType(String boundsType) throws LuaException {
        StorageBounds.BoundsType type = StorageBounds.BoundsType.getType(boundsType);
        if (type == StorageBounds.BoundsType.EMPTY) {
            String valids = String.join(", ", StorageBounds.BoundsType.valid.stream().map(t -> t.getLabel()).collect(Collectors.toList()));
            throw new LuaException("boundsType \"" + boundsType + "\" is not valid. Must be one of: " + valids);
        }
        return type;
    }

    public static ArrayList<Tuple<Vector3d, Vector3d>> getLineSegmentsFromLuaObject(Object obj) {
        ArrayList<Tuple<Vector3d, Vector3d>> lineSegments = new ArrayList<Tuple<Vector3d, Vector3d>>();

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

                        Vector3d start = new Vector3d(vecPrims[0], vecPrims[1], vecPrims[2]);
                        Vector3d end = new Vector3d(vecPrims[3], vecPrims[4], vecPrims[5]);

                        lineSegments.add(new Tuple<Vector3d, Vector3d>(start, end));
                    }
                }
            });
        }

        return lineSegments;
    }

    public static Object[] doRayTraceBatch(World world, ArrayList<Tuple<Vector3d, Vector3d>> lineSegments, boolean checkEntities) {
        ArrayList<Object> results = new ArrayList<>();
        lineSegments.forEach(tuple -> results.add(doRayTrace(world, tuple.getA(), tuple.getB(), checkEntities)));
        return results.toArray();
    }

    public static Map<String, Object> doRayTrace(World world, Vector3d start, Vector3d end, boolean checkEntities) {
        Map<String, Object> values = new HashMap<>();

        RayTraceResult result = world.clip(new RayTraceContext(start, end, RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, null));
        RayTraceResult.Type hitType = (result != null && result.getType() == RayTraceResult.Type.BLOCK) ? RayTraceResult.Type.BLOCK : RayTraceResult.Type.MISS;

        if (hitType == RayTraceResult.Type.BLOCK) {
            end = result.getLocation();
        }

        if (checkEntities) {
            AxisAlignedBB aabb = new AxisAlignedBB(start.x, start.y, start.z, end.x, end.y, end.z);
            List<Entity> entities = world.getEntitiesOfClass(Entity.class, aabb, entity -> true);

            double closestDistance = Double.POSITIVE_INFINITY;
            for (Entity e : entities) {
                Optional<Vector3d> entityResult = e.getBoundingBox().clip(start, end);
                if (entityResult.isPresent()) {
                    hitType = RayTraceResult.Type.ENTITY;
                    double dist = start.distanceTo(entityResult.get());
                    if (dist < closestDistance) {
                        closestDistance = dist;
                        result = new EntityRayTraceResult(e, entityResult.get());
                    }
                }
            }
        }

        if (result != null) {
            end = result.getLocation();
            values.put("hitPos", new Object[]{end.x, end.y, end.z});
            if (result instanceof BlockRayTraceResult) {
                BlockRayTraceResult blockResult = (BlockRayTraceResult) result;
                BlockPos blockPos = blockResult.getBlockPos();
                values.put("blockPos", new Object[]{blockPos.getX(), blockPos.getY(), blockPos.getZ()});
                values.put("side", (double) blockResult.getDirection().ordinal());
            } else {
                Entity entity = ((EntityRayTraceResult) result).getEntity();
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
        return doRayTrace(block.getLevel(), new Vector3d(startX, startY, startX), new Vector3d(endX, endY, endZ), checkEntities);
    }

    @LuaFunction
    public final Object[] rayTraceBatch(Object lineSegmentArray, boolean checkEntities) {
        ArrayList<Tuple<Vector3d, Vector3d>> lineSegments = getLineSegmentsFromLuaObject(lineSegmentArray);
        return doRayTraceBatch(block.getLevel(), lineSegments, checkEntities);
    }

    private ZoneManager getZoneManager() {
        return ZoneManager.get(((ServerWorld) block.getLevel()));
    }

}
