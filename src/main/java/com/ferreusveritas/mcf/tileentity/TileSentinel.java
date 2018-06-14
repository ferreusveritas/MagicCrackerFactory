package com.ferreusveritas.mcf.tileentity;

import com.ferreusveritas.mcf.blocks.BlockSentinel;
import com.ferreusveritas.mcf.util.CommandManager;
import com.ferreusveritas.mcf.util.MethodDescriptor;
import com.ferreusveritas.mcf.util.ZoneManager;

import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.world.World;

public class TileSentinel extends TileEntity implements IPeripheral, ITickable {
	
	public enum ComputerMethod {
		addBreakDenyBounds  ("snnnnnnn", true, "name", "minX", "minY", "minZ", "maxX", "maxY", "maxZ", "dim"),
		addPlaceDenyBounds  ("snnnnnnn", true, "name", "minX", "minY", "minZ", "maxX", "maxY", "maxZ", "dim"),
		addExplodeDenyBounds("snnnnnnn", true, "name", "minX", "minY", "minZ", "maxX", "maxY", "maxZ", "dim"),
		addSpawnDenyBounds  ("snnnnnnn", true, "name", "minX", "minY", "minZ", "maxX", "maxY", "maxZ", "dim"),
		remBreakDenyBounds  ("s", true, "name"),
		remPlaceDenyBounds  ("s", true, "name"),
		remExplodeDenyBounds("s", true, "name"),
		remSpawnDenyBounds  ("s", true, "name");
		
		final MethodDescriptor md;
		ComputerMethod(String argTypes, boolean cached, String ... args) { md = new MethodDescriptor(argTypes, cached, args); }
	}
	
	static CommandManager<ComputerMethod> commandManager = new CommandManager<>(ComputerMethod.class);
	
	@Override
	public void update() {
		
		BlockSentinel cartographer = (BlockSentinel)getBlockType();
		
		//Run commands that are cached that shouldn't be in the lua thread
		synchronized(commandManager.getCachedCommands()) {
			if(cartographer != null) {
				for(CommandManager<ComputerMethod>.CachedCommand cmd: commandManager.getCachedCommands()) {
					switch(cmd.method) {
					case addBreakDenyBounds  : ZoneManager.addBreakDenyBounds  ( cmd.s(), cmd.i(), cmd.i(), cmd.i(), cmd.i(), cmd.i(), cmd.i(), cmd.i() ); break;
					case addPlaceDenyBounds  : ZoneManager.addPlaceDenyBounds  ( cmd.s(), cmd.i(), cmd.i(), cmd.i(), cmd.i(), cmd.i(), cmd.i(), cmd.i() ); break;
					case addExplodeDenyBounds: ZoneManager.addExplodeDenyBounds( cmd.s(), cmd.i(), cmd.i(), cmd.i(), cmd.i(), cmd.i(), cmd.i(), cmd.i() ); break;
					case addSpawnDenyBounds  : ZoneManager.addSpawnDenyBounds  ( cmd.s(), cmd.i(), cmd.i(), cmd.i(), cmd.i(), cmd.i(), cmd.i(), cmd.i() ); break;
					case remBreakDenyBounds  : ZoneManager.remBreakDenyBounds(cmd.s()); break;
					case remPlaceDenyBounds  : ZoneManager.remPlaceDenyBounds(cmd.s()); break;
					case remExplodeDenyBounds: ZoneManager.remExplodeDenyBounds(cmd.s()); break;
					case remSpawnDenyBounds  : ZoneManager.remSpawnDenyBounds(cmd.s()); break;
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
		
		BlockSentinel cartographer = (BlockSentinel)getBlockType();
		World world = getWorld();
		
		if(!world.isRemote && cartographer != null) {
			ComputerMethod method = ComputerMethod.values()[methodNum];
			
			if(method.md.validateArguments(arguments)) {
				switch(method) {
					default:
						if(method.md.isCached()) {
							commandManager.cacheCommand(methodNum, arguments);
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
