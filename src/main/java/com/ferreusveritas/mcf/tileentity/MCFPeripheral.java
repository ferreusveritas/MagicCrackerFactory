package com.ferreusveritas.mcf.tileentity;

import com.ferreusveritas.mcf.util.AdvancedCommandManager;

import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;

public abstract class MCFPeripheral extends TileEntity implements IPeripheral, ITickable {

	private final String typeName;
	
	public MCFPeripheral(String typeName) {
		this.typeName = typeName;
	}
	
	public abstract AdvancedCommandManager getCommandManager();
	
	@Override
	public void update() {
		getCommandManager().runServerProcesses(getWorld(), this);
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
		return getCommandManager().callMethod(getWorld(), this, computer, context, methodNum, arguments);
	}
	
	@Override
	public boolean equals(IPeripheral other) {
		return this == other;
	}
	
}
