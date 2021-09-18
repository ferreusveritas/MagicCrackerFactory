package com.ferreusveritas.mcf.tileentity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.ferreusveritas.mcf.util.CommandManager;
import com.ferreusveritas.mcf.util.MethodDescriptor;
import com.ferreusveritas.mcf.util.MethodDescriptor.MethodDescriptorProvider;
import com.ferreusveritas.mcf.util.MethodDescriptor.SyncProcess;
import com.ferreusveritas.mcf.util.ZoneManager;
import com.ferreusveritas.mcf.util.bounds.BoundsStorage.EnumBoundsType;

import dan200.computercraft.api.lua.LuaException;
import net.minecraft.entity.Entity;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class TileSentinel extends MCFPeripheral {
	
	public static final String SENTINEL = "sentinel";
	
	public TileSentinel() {
		super(SENTINEL);
	}
	
	public static EnumBoundsType validateBoundsType(String boundsType) throws LuaException {
		EnumBoundsType type = EnumBoundsType.getType(boundsType);
		if(type == EnumBoundsType.EMPTY) {
			String valids = String.join(", ", EnumBoundsType.valid.stream().map(t -> t.getLabel()).collect(Collectors.toList()));
			throw new LuaException("boundsType \"" + boundsType + "\" is not valid. Must be one of: " + valids );
		}
		return type;
	}
	
	public enum ComputerMethod implements MethodDescriptorProvider {
		addCuboidBounds("ssnnnnnn", "boundsName,boundsType,minX,minY,minZ,maxX,maxY,maxZ",
				(world, peri, args) -> {
					String name = args.s();
					EnumBoundsType type = validateBoundsType(args.s());
					int x1 = args.i();
					int y1 = args.i();
					int z1 = args.i();
					int x2 = args.i();
					int y2 = args.i();
					int z2 = args.i();
					ZoneManager.get(world).addCuboidBounds(type, name, x1, y1, z1, x2, y2, z2);
					return new Object[0];
				}),
		
		addCylinderBounds("ssnnnnn", "boundsName,boundsType,posX,posZ,minY,maxY,radius",
				(world, peri, args) -> {
					String name = args.s();
					EnumBoundsType type = validateBoundsType(args.s());
					int x = args.i();
					int z = args.i();
					int y1 = args.i();
					int y2 = args.i();
					int radius = args.i();
					ZoneManager.get(world).addCylinderBounds(type, name, x, z, y1, y2, radius);
					return new Object[0];
				}),
		
		addAnyBounds("ss", "boundsName,boundsType",
				(world, peri, args) -> {
					String name = args.s();
					EnumBoundsType type = validateBoundsType(args.s());
					ZoneManager.get(world).addAnyBounds(type, name);
					return new Object[0];
				}),
		
		remBounds("s", "boundsName",
				(world, peri, args) -> {
					String name = args.s();
					ZoneManager.get(world).remBounds(name);
					return new Object[0];
				}),
		
		listBounds("", "",
				(world, peri, args) -> {
					return ZoneManager.get(world).listBounds();
				}),
		
		addEntityFilter("ssss", "boundsName,filterName,filterType,filterData",
				(world, peri, args) -> {
					String name = args.s();
					String filterName = args.s();
					String filterType = args.s();
					String filterData = args.s();
					ZoneManager.get(world).addEntityFilter(name, filterName, filterType, filterData);
					return new Object[0];
				}),
		
		remEntityFilter("ss", "boundsName,filterName",
				(world, peri, args) -> {
					String name = args.s();
					String filterName = args.s();
					ZoneManager.get(world).remEntityFilter(name, filterName);
					return new Object[0];
				}),
		
		getBoundsData("s", "boundsName", (world, peri, args) -> ZoneManager.get(world).getBoundsDataLua(args.s(0)) ),
		
		getEntitiesInBounds("s", "boundsName", (world, peri, args) -> ZoneManager.get(world).getEntitiesInBounds(world, args.s(), Entity.class)),
		
		getPlayersInBounds("s", "boundsName", (world, peri, args) -> ZoneManager.get(world).getPlayersInBounds(world, args.s()) ),
		
		rayTrace("nnnnnnb", "x1,y1,z1,x2,y2,z2,checkEntities", (world, peri, args) -> {
			return DoRayTrace(world, new Vec3d(args.d(), args.d(), args.d()), new Vec3d(args.d(), args.d(), args.d()), args.b());
		}),
		
		rayTraceBatch("ob", "segmentArray,checkEntities", (world, peri, args) -> {
			Object segmentObj = args.o(0);
			boolean checkEntities = args.b(1);
			ArrayList<Tuple<Vec3d, Vec3d>> lineSegments = getLineSegmentsFromLuaObject(segmentObj);
			return DoRayTraceBatch(world, lineSegments, checkEntities);
		});
		
		public static ArrayList<Tuple<Vec3d, Vec3d>> getLineSegmentsFromLuaObject(Object obj) {
			ArrayList<Tuple<Vec3d, Vec3d>> lineSegments = new ArrayList<Tuple<Vec3d,Vec3d>>();
			
			if(obj instanceof HashMap) {
				HashMap<?, ?> arrayMap = (HashMap<?, ?>) obj;
				
				arrayMap.forEach((k, subArray) -> {
					if(subArray instanceof HashMap) {
						HashMap<?,?> doubleMap = (HashMap<?, ?>) subArray;
						
						if(doubleMap.size() == 6) {
							double[] vecPrims = new double[6];
							
							for(int i = 0; i < 6; i++) {
								Object val = doubleMap.get((double)(i + 1));
								vecPrims[i] = val instanceof Double ? (Double) val : 0.0;
							}
							
							Vec3d start = new Vec3d(vecPrims[0], vecPrims[1], vecPrims[2]);
							Vec3d end = new Vec3d(vecPrims[3], vecPrims[4], vecPrims[5]);
							
							lineSegments.add(new Tuple<Vec3d, Vec3d>(start, end));
						}
					}
				});
			}
			
			return lineSegments;
		}
		
		public static Object[] DoRayTraceBatch(World world, ArrayList<Tuple<Vec3d, Vec3d>> lineSegments, boolean checkEntities) {
			ArrayList<Object> results = new ArrayList<>();
			
			lineSegments.forEach(tuple -> {
				Object[] result = DoRayTrace(world, tuple.getFirst(), tuple.getSecond(), checkEntities);
				results.add(result[0]);
			});
			
			return results.toArray(new Object[0]);
		}
		
		public static Object[] DoRayTrace(World world, Vec3d start, Vec3d end, boolean checkEntities) {
			Map<String, Object> values = new HashMap<>();
			
			RayTraceResult result = world.rayTraceBlocks(start, end);
			Type hitType = (result != null && result.typeOfHit == Type.BLOCK) ? Type.BLOCK : Type.MISS;
			
			if(hitType == Type.BLOCK) {
				end = result.hitVec;
			}
			
			if(checkEntities) {
				AxisAlignedBB aabb = new AxisAlignedBB(start.x, start.y, start.z, end.x, end.y, end.z);
				List<Entity> entities = world.getEntitiesWithinAABB(Entity.class, aabb);
				
				double closestDistance = Double.POSITIVE_INFINITY;
				for(Entity e : entities) {
					RayTraceResult entityResult = e.getEntityBoundingBox().calculateIntercept(start, end);
					if(entityResult != null) {
						hitType = Type.ENTITY;
						double dist = start.distanceTo(entityResult.hitVec);
						if(dist < closestDistance) {
							closestDistance = dist;
							result = entityResult;
							result.entityHit = e;
						}
					}
				}
			}
			
			if(result != null) {
				end = result.hitVec;
				BlockPos blockPos = result.getBlockPos();
				values.put("hitPos", new Object[] { end.x, end.y, end.z } );
				if(blockPos != null && blockPos != BlockPos.ORIGIN) {
					values.put("blockPos", new Object[] { blockPos.getX(), blockPos.getY(), blockPos.getZ() } );
				}
				values.put("side", (double)result.sideHit.ordinal());
				if(result.entityHit != null) {
					values.put("entity", result.entityHit.getName());
					values.put("class", result.entityHit.getClass().getSimpleName());
					values.put("id", result.entityHit.getEntityId());
				}
			}
			
			values.put("type", hitType.name());
			
			return new Object[] { values };
		}
		
		final MethodDescriptor md;
		private ComputerMethod(String argTypes, String args, SyncProcess process) 
		{ md = new MethodDescriptor(toString(), argTypes, args, process); }
		
		public static TileSentinel getTool(MCFPeripheral peripheral) {
			return (TileSentinel) peripheral;
		}
		
		@Override
		public MethodDescriptor getMethodDescriptor() {
			return md;
		}
		
	}
	
	static CommandManager<ComputerMethod> commandManager = new CommandManager<>(ComputerMethod.class);
	
	@Override
	public CommandManager getCommandManager() {
		return commandManager;
	}
	
}
