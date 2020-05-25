package com.ferreusveritas.mcf.tileentity;

import java.util.ArrayList;
import java.util.List;

import com.ferreusveritas.mcf.util.Arguments;
import com.ferreusveritas.mcf.util.CommandManager;
import com.ferreusveritas.mcf.util.CommandManager.SyncCommand;

import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;

public abstract class MCFPeripheral extends TileEntity implements IPeripheral, ITickable {

	private final String typeName;
	
	protected List<SyncCommand> syncRequests = new ArrayList<>();
	
	public MCFPeripheral(String typeName) {
		this.typeName = typeName;
	}
	
	public abstract CommandManager getCommandManager();
	
	protected static Object[] obj(Object ... args) {
		return args;
	}

	public void addSyncRequest(SyncCommand command) {
		synchronized (syncRequests) {
			syncRequests.add(command);
		}
	}
	
	@Override
	public void update() {
		if(!getWorld().isRemote) {
			synchronized (syncRequests) {
				for(SyncCommand syncReq: syncRequests) {
					syncReq.serverProcess(getWorld(), this);
				}
				syncRequests.clear();
			}
		}
	}
		
	
	@Override
	public String getType() {
		return typeName;
	}
	
	@Override
	public String[] getMethodNames() {
		return getCommandManager().getMethodNames();
	}

	@Override
	public Object[] callMethod(IComputerAccess computer, ILuaContext context, int methodNum, Object[] arguments) throws LuaException {
		return getCommandManager().callMethod(getWorld(), this, computer, context, methodNum, new Arguments(arguments));
	}
	
	@Override
	public boolean equals(IPeripheral other) {
		return this == other;
	}
	
}
