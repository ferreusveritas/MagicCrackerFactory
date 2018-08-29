package com.ferreusveritas.mcf.tileentity;

import com.ferreusveritas.mcf.util.CommandManager;
import com.ferreusveritas.mcf.util.MethodDescriptor;
import com.ferreusveritas.mcf.util.MethodDescriptor.SyncProcess;
import com.ferreusveritas.mcf.util.ZoneManager;
import com.ferreusveritas.mcf.util.bounds.BoundsStorage.EnumBoundsType;

public class TileSentinel extends MCFPeripheral {
	
	public TileSentinel() {
		super("sentinel");
	}

	public enum ComputerMethod {
		addCuboidBounds("ssnnnnnn", "type, name, minX, minY, minZ, maxX, maxY, maxZ",
				(world, peri, args) -> {
					EnumBoundsType type = EnumBoundsType.getType(getStr(args, 0));
					String name = getStr(args, 1);
					int x1 = getInt(args, 2);
					int y1 = getInt(args, 3);
					int z1 = getInt(args, 4);
					int x2 = getInt(args, 5);
					int y2 = getInt(args, 6);
					int z2 = getInt(args, 7);
					ZoneManager.get(world).addCuboidBounds(type, name, x1, y1, z1, x2, y2, z2);
					return new Object[0];
				}),
		
		addCylinderBounds("ssnnnnn", "type, name, posX, posZ, minY, maxY, radius",
				(world, peri, args) -> {
					EnumBoundsType type = EnumBoundsType.getType(getStr(args, 0));
					String name = getStr(args, 1);
					int x = getInt(args, 2);
					int z = getInt(args, 3);
					int y1 = getInt(args, 4);
					int y2 = getInt(args, 5);
					int radius = getInt(args, 6);
					ZoneManager.get(world).addCylinderBounds(type, name, x, z, y1, y2, radius);
					return new Object[0];					
				}),
		
		addAnyBounds("ss", "type, name",
				(world, peri, args) -> {
					EnumBoundsType type = EnumBoundsType.getType(getStr(args, 0));
					String name = getStr(args, 1);
					ZoneManager.get(world).addAnyBounds(type, name);
					return new Object[0];
				}),
		
		remBounds("ss", "type, name",
				(world, peri, args) -> {
					EnumBoundsType type = EnumBoundsType.getType(getStr(args, 0));
					String name = getStr(args, 1);
					ZoneManager.get(world).remBounds(type, name);
					return new Object[0];
				}),
		
		listBounds("s", "type",
				(world, peri, args) -> {
					return ZoneManager.get(world).listBounds( EnumBoundsType.getType(getStr(args, 0)) );
				}),
		
		addEntityFilter("sssss", "type, name, filtername, filtertype, filterdata",
				(world, peri, args) -> {
					EnumBoundsType type = EnumBoundsType.getType(getStr(args, 0));
					String name = getStr(args, 1);
					String filterName = getStr(args, 2);
					String filterType = getStr(args, 3);
					String filterData = getStr(args, 4);
					ZoneManager.get(world).addEntityFilter(type, name, filterName, filterType, filterData);
					return new Object[0];
				}),
		
		remEntityFilter("sss", "type, name, filtername",
				(world, peri, args) -> {
					EnumBoundsType type = EnumBoundsType.getType(getStr(args, 0));
					String name = getStr(args, 1);
					String filterName = getStr(args, 2);
					ZoneManager.get(world).remEntityFilter(type, name, filterName);
					return new Object[0];
				}),
		
		getBoundsData("ss", "type, name",
				(world, peri, args) -> {
					return ZoneManager.get(world).getBoundsDataLua(EnumBoundsType.getType(getStr(args, 0)), getStr(args, 1));
				}),
		
		getPlayersInBounds("s", "name",
				(world, peri, args) -> {
					return ZoneManager.get(world).getPlayersInBounds(world, getStr(args, 0));
				});
		
		final MethodDescriptor md;
		private ComputerMethod(String argTypes, String args, SyncProcess process) { md = new MethodDescriptor(argTypes, args, process); }
		
		public static TileSentinel getTool(MCFPeripheral peripheral) {
			return (TileSentinel) peripheral;
		}
		
		public static String getStr(Object[] args, int arg) {
			return (String) args[arg];
		}
		
		public static int getInt(Object[] args, int arg) {
			return ((Double)args[arg]).intValue();
		}
	}
	
	static CommandManager<ComputerMethod> commandManager = new CommandManager<>(ComputerMethod.class);
	
	@Override
	public CommandManager getCommandManager() {
		return commandManager;
	}
	
}
