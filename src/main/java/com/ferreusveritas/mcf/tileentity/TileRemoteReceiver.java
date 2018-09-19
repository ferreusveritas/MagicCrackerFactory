package com.ferreusveritas.mcf.tileentity;

import com.ferreusveritas.mcf.util.CommandManager;
import com.ferreusveritas.mcf.util.MethodDescriptor;
import com.ferreusveritas.mcf.util.MethodDescriptor.MethodDescriptorProvider;
import com.ferreusveritas.mcf.util.MethodDescriptor.SyncProcess;

public class TileRemoteReceiver extends MCFPeripheral  {
	
	public TileRemoteReceiver() {
		super("remotereceiver");
	}
	
	public enum ComputerMethod implements MethodDescriptorProvider {
		connect("", "", (world, peri, args) -> obj()),
		disconnect("", "", (world, peri, args) -> obj()),
		getMapNum("n", "mapNum", (world, peri, args) -> obj("nothing") );
		
		final MethodDescriptor md;
		private ComputerMethod(String argTypes, String args, SyncProcess process) { md = new MethodDescriptor(toString(), argTypes, args, process); }
		
		public static TileRemoteReceiver getTool(MCFPeripheral peripheral) {
			return (TileRemoteReceiver) peripheral;
		}
		
		@Override
		public MethodDescriptor getMethodDescriptor() {
			return md;
		}
		
	}
	
	static CommandManager<ComputerMethod> commandManager = new CommandManager<>(ComputerMethod.class);
	
	@Override
	public CommandManager getCommandManager() {
		return commandManager;
	}
	
}
