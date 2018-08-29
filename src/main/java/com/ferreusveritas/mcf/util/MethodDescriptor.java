package com.ferreusveritas.mcf.util;

import com.ferreusveritas.mcf.tileentity.CCDataType;
import com.ferreusveritas.mcf.tileentity.MCFPeripheral;

import dan200.computercraft.api.lua.LuaException;
import net.minecraft.world.World;

public class MethodDescriptor {
	
	public interface SyncProcess {
		Object[] apply(World world, MCFPeripheral peripheral, Arguments args);
	}
	
	private final String argTypes;
	private final String args[];
	private final SyncProcess process;

	public MethodDescriptor(String argTypes, String args, SyncProcess process) {
		this.argTypes = argTypes;
		this.args = args.split("[0-9a-zA-Z]+(,[0-9a-zA-Z]+)*");
		this.process = process;
	}

	public SyncProcess getProcess() {
		return process;
	}
	
	public boolean isValidArguments(Arguments arguments) {
		if(arguments.getNumArgs() >= argTypes.length()) {
			for (int i = 0; i < argTypes.length(); i++){
				if(!CCDataType.byIdent(argTypes.charAt(i)).isInstance(arguments.o(i))) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

	public boolean validateArguments(Arguments arguments) throws LuaException {
		if(isValidArguments(arguments)) {
			return true;
		}
		throw new LuaException(invalidArgumentsError());
	}

	public String invalidArgumentsError() {
		String error = "Expected: " + this.toString();
		for (int i = 0; i < argTypes.length(); i++){
			error += " " + args[i] + "<" + CCDataType.byIdent(argTypes.charAt(i)).name + ">";
		}
		return error;
	}
	
}
