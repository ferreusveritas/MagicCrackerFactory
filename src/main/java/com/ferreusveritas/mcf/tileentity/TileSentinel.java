package com.ferreusveritas.mcf.tileentity;

import com.ferreusveritas.mcf.blocks.BlockPeripheral;
import com.ferreusveritas.mcf.util.CommandManager;
import com.ferreusveritas.mcf.util.MethodDescriptor;
import com.ferreusveritas.mcf.util.ZoneManager;
import com.ferreusveritas.mcf.util.bounds.BoundsStorage.EnumBoundsType;

import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.world.World;

public class TileSentinel extends TileEntity implements IPeripheral, ITickable {
	
	public enum ComputerMethod {
		addCuboidBounds("ssnnnnnn", true, "type", "name", "minX", "minY", "minZ", "maxX", "maxY", "maxZ"),
		addCylinderBounds("ssnnnnn", true, "type", "name", "posX", "posZ", "minY", "maxY", "radius"),
		addAnyBounds("ss", true, "type", "name"),
		remBounds("ss", true, "type", "name"),
		listBounds("s", false, "type"),
		addEntityFilter("sssss", true, "type", "name", "filtername", "filtertype", "filterdata"),
		remEntityFilter("sss", true, "type", "name", "filtername"),
		getBoundsData("ss", false, "type", "name"),
		getPlayersInBounds("s", false, "name");
		
		final MethodDescriptor md;
		ComputerMethod(String argTypes, boolean cached, String ... args) { md = new MethodDescriptor(argTypes, cached, args); }
	}
	
	static CommandManager<ComputerMethod> commandManager = new CommandManager<>(ComputerMethod.class);
	
	@Override
	public void update() {
		
		BlockPeripheral cartographer = (BlockPeripheral)getBlockType();
		
		//Run commands that are cached that shouldn't be in the lua thread
		synchronized(commandManager) {
			if(cartographer != null) {
				for(CommandManager<ComputerMethod>.CachedCommand cmd: commandManager.getCachedCommands()) {
					switch(cmd.method) {
					case addCuboidBounds: ZoneManager.get(world).addCuboidBounds(EnumBoundsType.getType(cmd.s()), cmd.s(), cmd.i(), cmd.i(), cmd.i(), cmd.i(), cmd.i(), cmd.i() ); break;
					case addCylinderBounds: ZoneManager.get(world).addCylinderBounds(EnumBoundsType.getType(cmd.s()), cmd.s(), cmd.i(), cmd.i(), cmd.i(), cmd.i(), cmd.i() ); break;
					case addAnyBounds: ZoneManager.get(world).addAnyBounds(EnumBoundsType.getType(cmd.s()), cmd.s() ); break;
					case remBounds: ZoneManager.get(world).remBounds(EnumBoundsType.getType(cmd.s()), cmd.s() ); break;
					case addEntityFilter: ZoneManager.get(world).addEntityFilter(EnumBoundsType.getType(cmd.s()), cmd.s(), cmd.s(), cmd.s(), cmd.s()); break;
					case remEntityFilter: ZoneManager.get(world).remEntityFilter(EnumBoundsType.getType(cmd.s()), cmd.s(), cmd.s()); break;
					default: break;
					}
				}
				commandManager.clear();
			}
		}
		
	}
	
	@Override
	public String getType() {
		return "sentinel";
	}
	
	@Override
	public String[] getMethodNames() {
		return commandManager.getMethodNames();
	}
	
	/**
	* I hear ya Dan!  Make the function threadsafe by caching the commmands to run in the main world server thread and not the lua thread.
	*/
	@Override
	public Object[] callMethod(IComputerAccess computer, ILuaContext context, int methodNum, Object[] arguments) throws LuaException {
		if(methodNum < 0 || methodNum >= commandManager.getNumMethods()) {
			throw new IllegalArgumentException("Invalid method number");
		}
		
		BlockPeripheral cartographer = (BlockPeripheral)getBlockType();
		World world = getWorld();
		
		if(!world.isRemote && cartographer != null) {
			ComputerMethod method = ComputerMethod.values()[methodNum];
			
			if(method.md.validateArguments(arguments)) {
				switch(method) {
					case listBounds: return ZoneManager.get(world).listBounds( EnumBoundsType.getType((String) arguments[0]) );
					case getBoundsData: return ZoneManager.get(world).getBoundsDataLua(EnumBoundsType.getType((String) arguments[0]), (String) arguments[1]);
					case getPlayersInBounds: return ZoneManager.get(world).getPlayersInBounds(world, (String) arguments[0]);
					default:
						if(method.md.isCached()) {
							synchronized(commandManager) {
								commandManager.cacheCommand(methodNum, arguments);
							}
						}
				}
			}
		}
		
		return null;
	}
	
	@Override
	public boolean equals(IPeripheral other) {
		return this == other;
	}
	
}
