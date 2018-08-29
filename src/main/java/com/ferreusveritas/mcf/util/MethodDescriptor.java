package com.ferreusveritas.mcf.util;

import java.util.function.BiFunction;

import com.ferreusveritas.mcf.tileentity.CCDataType;

import dan200.computercraft.api.lua.LuaException;
import net.minecraft.world.World;

public class MethodDescriptor {
	
	private final String argTypes;
	private final String args[];
	private final boolean cached;
	private final BiFunction<World, Object[], Object[]> process;

	public MethodDescriptor(String argTypes, boolean cached, String ... args) {
		this.argTypes = argTypes;
		this.args = args;
		this.cached = cached;
		this.process = null;
	}

	public MethodDescriptor(String argTypes, String args, BiFunction<World, Object[], Object[]> process) {
		this.argTypes = argTypes;
		this.args = args.split("[0-9a-zA-Z]+(,[0-9a-zA-Z]+)*");
		this.cached = true;
		this.process = process;
	}

	public boolean isCached() {
		return cached;
	}

	public BiFunction<World, Object[], Object[]> getProcess() {
		return process;
	}
	
	public boolean isValidArguments(Object[] arguments) {
		if(arguments.length >= argTypes.length()) {
			for (int i = 0; i < argTypes.length(); i++){
				if(!CCDataType.byIdent(argTypes.charAt(i)).isInstance(arguments[i])) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

	public boolean validateArguments(Object[] arguments) throws LuaException {
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
