package com.ferreusveritas.mcf.tileentity;

import com.ferreusveritas.mcf.util.CommandManager;
import com.ferreusveritas.mcf.util.MethodDescriptor;
import com.ferreusveritas.mcf.util.MethodDescriptor.MethodDescriptorProvider;
import com.ferreusveritas.mcf.util.MethodDescriptor.SyncProcess;
import com.ferreusveritas.mcf.util.ZoneManager;
import com.ferreusveritas.mcf.util.bounds.BoundsStorage.EnumBoundsType;

public class TileSentinel extends MCFPeripheral {
	
	public TileSentinel() {
		super("sentinel");
	}

	public enum ComputerMethod implements MethodDescriptorProvider {
		addCuboidBounds("ssnnnnnn", "type,name,minX,minY,minZ,maxX,maxY,maxZ",
				(world, peri, args) -> {
					EnumBoundsType type = EnumBoundsType.getType(args.s());
					String name = args.s();
					int x1 = args.i();
					int y1 = args.i();
					int z1 = args.i();
					int x2 = args.i();
					int y2 = args.i();
					int z2 = args.i();
					ZoneManager.get(world).addCuboidBounds(type, name, x1, y1, z1, x2, y2, z2);
					return new Object[0];
				}),
		
		addCylinderBounds("ssnnnnn", "type,name,posX,posZ,minY,maxY,radius",
				(world, peri, args) -> {
					EnumBoundsType type = EnumBoundsType.getType(args.s());
					String name = args.s();
					int x = args.i();
					int z = args.i();
					int y1 = args.i();
					int y2 = args.i();
					int radius = args.i();
					ZoneManager.get(world).addCylinderBounds(type, name, x, z, y1, y2, radius);
					return new Object[0];					
				}),
		
		addAnyBounds("ss", "type,name",
				(world, peri, args) -> {
					EnumBoundsType type = EnumBoundsType.getType(args.s());
					String name = args.s();
					ZoneManager.get(world).addAnyBounds(type, name);
					return new Object[0];
				}),
		
		remBounds("ss", "type,name",
				(world, peri, args) -> {
					EnumBoundsType type = EnumBoundsType.getType(args.s());
					String name = args.s();
					ZoneManager.get(world).remBounds(type, name);
					return new Object[0];
				}),
		
		listBounds("s", "type",
				(world, peri, args) -> {
					return ZoneManager.get(world).listBounds( EnumBoundsType.getType(args.s()));
				}),
		
		addEntityFilter("sssss", "type,name,filtername,filtertype,filterdata",
				(world, peri, args) -> {
					EnumBoundsType type = EnumBoundsType.getType(args.s());
					String name = args.s();
					String filterName = args.s();
					String filterType = args.s();
					String filterData = args.s();
					ZoneManager.get(world).addEntityFilter(type, name, filterName, filterType, filterData);
					return new Object[0];
				}),
		
		remEntityFilter("sss", "type,name,filtername",
				(world, peri, args) -> {
					EnumBoundsType type = EnumBoundsType.getType(args.s());
					String name = args.s();
					String filterName = args.s();
					ZoneManager.get(world).remEntityFilter(type, name, filterName);
					return new Object[0];
				}),
		
		getBoundsData("ss", "type,name", (world, peri, args) -> ZoneManager.get(world).getBoundsDataLua(EnumBoundsType.getType(args.s(0)), args.s(1)) ),
		
		getPlayersInBounds("s", "name", (world, peri, args) -> ZoneManager.get(world).getPlayersInBounds(world, args.s()) );
		
		final MethodDescriptor md;
		private ComputerMethod(String argTypes, String args, SyncProcess process) { md = new MethodDescriptor(toString(), argTypes, args, process); }
		
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
