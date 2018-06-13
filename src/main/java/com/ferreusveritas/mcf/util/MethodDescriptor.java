package com.ferreusveritas.mcf.util;

import com.ferreusveritas.mcf.tileentity.CCDataType;

import dan200.computercraft.api.lua.LuaException;

public class MethodDescriptor {
	
	private final String argTypes;
	private final String args[];
	private final boolean cached;

	public MethodDescriptor(String argTypes, boolean cached, String ... args) {
		this.argTypes = argTypes;
		this.args = args;
		this.cached = cached;
	}

	public boolean isCached() {
		return cached;
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
