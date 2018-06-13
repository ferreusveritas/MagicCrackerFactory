package com.ferreusveritas.mcf.tileentity;

import com.ferreusveritas.mcf.blocks.BlockCartographer;
import com.ferreusveritas.mcf.event.SecurityHandler;
import com.ferreusveritas.mcf.util.CommandManager;
import com.ferreusveritas.mcf.util.MethodDescriptor;

import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.world.World;

public class TileSentinel extends TileEntity implements IPeripheral, ITickable {
	
	public enum ComputerMethod {
		addBreakDenyBounds  ("nnnnnnn", true, "minX", "minY", "minZ", "maxX", "maxY", "maxZ", "dim"),
		addPlaceDenyBounds  ("nnnnnnn", true, "minX", "minY", "minZ", "maxX", "maxY", "maxZ", "dim"),
		addExplodeDenyBounds("nnnnnnn", true, "minX", "minY", "minZ", "maxX", "maxY", "maxZ", "dim"),
		addSpawnDenyBounds  ("nnnnnnn", true, "minX", "minY", "minZ", "maxX", "maxY", "maxZ", "dim"),
		remBreakDenyBounds  ("nnnnnnn", true, "minX", "minY", "minZ", "maxX", "maxY", "maxZ", "dim"),
		remPlaceDenyBounds  ("nnnnnnn", true, "minX", "minY", "minZ", "maxX", "maxY", "maxZ", "dim"),
		remExplodeDenyBounds("nnnnnnn", true, "minX", "minY", "minZ", "maxX", "maxY", "maxZ", "dim"),
		remSpawnDenyBounds  ("nnnnnnn", true, "minX", "minY", "minZ", "maxX", "maxY", "maxZ", "dim");
		
		final MethodDescriptor md;
		ComputerMethod(String argTypes, boolean cached, String ... args) { md = new MethodDescriptor(argTypes, cached, args); }
	}
	
	static CommandManager<ComputerMethod> commandManager = new CommandManager<>(ComputerMethod.class);
	
	@Override
	public void update() {
		
		BlockCartographer cartographer = (BlockCartographer)getBlockType();
		
		//Run commands that are cached that shouldn't be in the lua thread
		synchronized(commandManager.getCachedCommands()) {
			if(cartographer != null) {
				for(CommandManager<ComputerMethod>.CachedCommand cmd: commandManager.getCachedCommands()) {
					switch(cmd.method) {
					case addBreakDenyBounds  : SecurityHandler.addBreakDenyBounds  ( cmd.i(), cmd.i(), cmd.i(), cmd.i(), cmd.i(), cmd.i(), cmd.i() ); break;
					case addPlaceDenyBounds  : SecurityHandler.addPlaceDenyBounds  ( cmd.i(), cmd.i(), cmd.i(), cmd.i(), cmd.i(), cmd.i(), cmd.i() ); break;
					case addExplodeDenyBounds: SecurityHandler.addExplodeDenyBounds( cmd.i(), cmd.i(), cmd.i(), cmd.i(), cmd.i(), cmd.i(), cmd.i() ); break;
					case addSpawnDenyBounds  : SecurityHandler.addSpawnDenyBounds  ( cmd.i(), cmd.i(), cmd.i(), cmd.i(), cmd.i(), cmd.i(), cmd.i() ); break;
					case remBreakDenyBounds  : cmd.i(); break;
					case remPlaceDenyBounds  : cmd.i(); break;
					case remExplodeDenyBounds: cmd.i(); break;
					case remSpawnDenyBounds  : cmd.i(); break;				
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
		
		BlockCartographer cartographer = (BlockCartographer)getBlockType();
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
