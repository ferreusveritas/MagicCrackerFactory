package com.ferreusveritas.mcf.tileentity;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.ferreusveritas.mcf.util.CommandManager;
import com.ferreusveritas.mcf.util.MethodDescriptor;
import com.ferreusveritas.mcf.util.MethodDescriptor.MethodDescriptorProvider;
import com.ferreusveritas.mcf.util.MethodDescriptor.SyncProcess;

import dan200.computercraft.api.peripheral.IComputerAccess;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class TileRemoteReceiver extends MCFPeripheral  {
	
	private static Set<TileRemoteReceiver> connections = new HashSet<>();
	private Set<IComputerAccess> computers = new HashSet<>();
	
	private boolean isInterdimensional = false;
	
	public TileRemoteReceiver() {
		super("remotereceiver");
	}
	
	public static void broadcastRemoteEvents(EntityPlayer player, String remoteId, Vec3d hitPos, BlockPos blockPos, EnumFacing face) {
		
		Iterator<TileRemoteReceiver> i = connections.iterator();
		
		while(i.hasNext()) {
			TileRemoteReceiver receiver = i.next();
			if(receiver.isInterdimensional || player.world.provider.getDimension() == receiver.world.provider.getDimension()) { //Make sure player is in the same world as the receiver
				if(receiver.world.isBlockLoaded(receiver.getPos())) {
					receiver.createRemoteEvent(player, remoteId, hitPos, blockPos, face);
				} else {
					i.remove();
				}
			}
		}
	}
	
	public void createRemoteEvent(EntityPlayer player, String remoteId, Vec3d hitPos, BlockPos blockPos, EnumFacing face) {
		Map<String, Double> hitPosMap = new HashMap<>();
		hitPosMap.put("x", hitPos.x);
		hitPosMap.put("y", hitPos.y);
		hitPosMap.put("z", hitPos.z);

		Map<String, Integer> blockPosMap = new HashMap<>();
		blockPosMap.put("x", blockPos.getX());
		blockPosMap.put("y", blockPos.getY());
		blockPosMap.put("z", blockPos.getZ());
		
		Integer faceNum = face != null ? face.ordinal() : null;
		
		Object arguments[] = { player.getName(), remoteId, hitPosMap, blockPosMap, faceNum };
		for( IComputerAccess comp : computers) {
			comp.queueEvent("remote_control", arguments);
		}
	}
	
	public enum ComputerMethod implements MethodDescriptorProvider {
		connect("", "", (world, peri, args) -> obj(getTool(peri).connect())),
		disconnect("", "", (world, peri, args) -> obj(getTool(peri).disconnect())),
		setInterdimensional("", "", (world, peri, args) -> obj(getTool(peri).setInterdimensional(args.b())));
		
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
	
	public int connect() {
		connections.add(this);
		return 0;
	}
	
	public int disconnect() {
		connections.remove(this);
		return 0;
	}
	
	public int setInterdimensional(boolean isInterdimensional) {
		this.isInterdimensional = isInterdimensional;
		return 0;
	}
	
	static CommandManager<ComputerMethod> commandManager = new CommandManager<>(ComputerMethod.class);
	
	@Override
	public CommandManager getCommandManager() {
		return commandManager;
	}
	
	@Override
	public void attach(IComputerAccess computer) {
		computers.add(computer);
	}
	
	@Override
	public void detach(IComputerAccess computer) {
		computers.remove(computer);
	}
	
}
