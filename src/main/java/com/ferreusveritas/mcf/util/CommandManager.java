package com.ferreusveritas.mcf.util;

import java.util.ArrayList;

public class CommandManager<E extends Enum<E>> {

	private ArrayList<CachedCommand> cachedCommands = new ArrayList<>(1);
	private final int numMethods;
	private final String[] methodNames;
	private Class type;
	
	public CommandManager(Class<E> e) {
		numMethods = e.getEnumConstants().length;
		methodNames = new String[numMethods];
		type = e;
		
		for(E method : e.getEnumConstants()) { 
			methodNames[method.ordinal()] = method.toString(); 
		}
	}

	public void cacheCommand(int method, Object[] args) {
		synchronized (cachedCommands) {
			cachedCommands.add(new CachedCommand(method, args));
		}
	}
	
	public ArrayList<CachedCommand > getCachedCommands() {
		return cachedCommands;
	}
	
	public void clear() {
		cachedCommands.clear();
	}
	
	public String[] getMethodNames() {
		return methodNames;
	}
	
	public int getNumMethods() {
		return numMethods;
	}
	
	public class CachedCommand {
		public E method;
		Object[] arguments;
		int argRead = 0;
		
		public CachedCommand(int method, Object[] args) {
			this.method = (E) type.getEnumConstants()[method];
			this.arguments = args;
		}
		
		public double d() {
			return ((Double)arguments[argRead++]).doubleValue();
		}
		
		public int i() {
			return ((Double)arguments[argRead++]).intValue();
		}
		
		public String s() {
			return ((String)arguments[argRead++]);
		}
		
		public boolean b() {
			return ((Boolean)arguments[argRead++]).booleanValue();
		}
		
	}
}