package com.ferreusveritas.mcf.util;

import com.ferreusveritas.mcf.tileentity.MCFPeripheral;
import com.ferreusveritas.mcf.util.MethodDescriptor.MethodDescriptorProvider;
import com.ferreusveritas.mcf.util.MethodDescriptor.SyncProcess;

import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import net.minecraft.world.World;

public class CommandManager<E extends Enum<E>> {
	
	private final int numMethods;
	private final String[] methodNames;
	private final MethodDescriptor[] methodDesc;
	
	public CommandManager(Class<E> e) {
		numMethods = e.getEnumConstants().length;
		methodNames = new String[numMethods];
		methodDesc = new MethodDescriptor[numMethods];
		
		for(E method : e.getEnumConstants()) { 
			methodNames[method.ordinal()] = method.toString();
			methodDesc[method.ordinal()] = ((MethodDescriptorProvider)method).getMethodDescriptor();
		}
	}
	
	public String[] getMethodNames() {
		return methodNames;
	}
	
	public int getNumMethods() {
		return numMethods;
	}
	
	public class SyncCommand {
		private boolean fulfilled;
		private Object[] result;
		private Arguments args;
		private final SyncProcess processor;
		private LuaException luaException;
		
		public SyncCommand(SyncProcess processor, Arguments args) {
			this.processor = processor;
			this.args = args;
		}
		
		public synchronized void serverProcess(World world, MCFPeripheral peripheral) {
			if(!fulfilled) {
				try {
					result = processor.apply(world, peripheral, args);
				} catch (LuaException e) {
					luaException = e;
				}
				fulfilled = true;
				notifyAll();
			}
		}
		
		public synchronized Object[] getResult() throws LuaException {
			while(!fulfilled) {
				try {
					wait();
				} catch (InterruptedException e) {}
			}
			if(luaException != null) {
				throw luaException;
			}
			return result;
		}
		
	}
	
	/**
	 * I hear ya Dan!  Make the function threadsafe by caching the commmands to run in the main world server thread and not the lua thread.
	 */
	public Object[] callMethod(World world, MCFPeripheral peripheral, IComputerAccess computer, ILuaContext context, int methodNum, Arguments arguments) throws LuaException {
		
		if(!world.isRemote) {
			if(peripheral.getBlockType() != null) {
				if(methodNum < 0 || methodNum >= getNumMethods()) {
					throw new IllegalArgumentException("Invalid method number");
				}
				
				MethodDescriptor md = methodDesc[methodNum];
				if(md.validateArguments(arguments)) {
					return serverProcess(md.getProcess(), arguments, peripheral, md.isSynced());
				}
			}
		}
		
		return null;
	}
	
	/**
	 * 
	 * @param process The process to run
	 * @param args The argument data for the process
	 * @param synced Determines weather or not we wait for the function to complete.
	 * @return
	 * @throws LuaException 
	 */
	public Object[] serverProcess(SyncProcess process, Arguments args, MCFPeripheral peripheral, boolean synced) throws LuaException {
		SyncCommand syncReq = new SyncCommand(process, args);
		peripheral.addSyncRequest(syncReq);
		return synced ? syncReq.getResult() : new Object[0];
	}
	
}