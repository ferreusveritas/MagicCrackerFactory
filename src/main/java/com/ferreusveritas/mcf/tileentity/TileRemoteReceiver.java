package com.ferreusveritas.mcf.tileentity;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.ferreusveritas.mcf.blocks.BlockTouchButton;
import com.ferreusveritas.mcf.command.CommandProx;
import com.ferreusveritas.mcf.util.CommandManager;
import com.ferreusveritas.mcf.util.MethodDescriptor;
import com.ferreusveritas.mcf.util.MethodDescriptor.MethodDescriptorProvider;
import com.ferreusveritas.mcf.util.MethodDescriptor.SyncProcess;
import com.ferreusveritas.mcf.util.bounds.BoundsCuboid;

import dan200.computercraft.api.peripheral.IComputerAccess;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class TileRemoteReceiver extends MCFPeripheral {

	public static final String REMOTERECEIVER = "remotereceiver";

	private static Set<TileRemoteReceiver> connections = new HashSet<>();
	private Set<IComputerAccess> computers = new HashSet<>();

	private boolean isInterdimensional = false;

	private BoundsCuboid bounds = null;

	public TileRemoteReceiver() {
		super(REMOTERECEIVER);
	}

	public static void broadcastRemoteEvents(EntityPlayer player, String remoteId, Vec3d hitPos, BlockPos blockPos, EnumFacing face) {

		Iterator<TileRemoteReceiver> i = connections.iterator();

		while(i.hasNext()) {
			TileRemoteReceiver receiver = i.next();
			if(receiver.isInBounds(blockPos)) {
				if(receiver.isInterdimensional || player.world.provider.getDimension() == receiver.world.provider.getDimension()) { //Make sure player is in the same world as the receiver
					if(receiver.world.isBlockLoaded(receiver.getPos())) {
						receiver.createRemoteEvent(player, remoteId, hitPos, blockPos, face);
					} else {
						i.remove();
					}
				}
			}
		}
	}

	public static void broadcastTouchMapEvents(EntityPlayer player, ItemStack heldItem, Vec3d hitPos, BlockPos blockPos, EnumFacing face) {

		Iterator<TileRemoteReceiver> i = connections.iterator();

		while(i.hasNext()) {
			TileRemoteReceiver receiver = i.next();
			if(receiver.isInBounds(blockPos)) {
				if(receiver.isInterdimensional || player.world.provider.getDimension() == receiver.world.provider.getDimension()) { //Make sure player is in the same world as the receiver
					if(receiver.world.isBlockLoaded(receiver.getPos())) {
						receiver.createTouchMapEvent(player, heldItem, hitPos, blockPos, face);
					} else {
						i.remove();
					}
				}
			}
		}
	}
	
	public static void broadcastProxyEvents(EntityPlayer player, String[] commands) {

		Iterator<TileRemoteReceiver> i = connections.iterator();

		while(i.hasNext()) {
			TileRemoteReceiver receiver = i.next();
			if(receiver.isInBounds(player.getPosition())) {
				if(receiver.isInterdimensional || player.world.provider.getDimension() == receiver.world.provider.getDimension()) { //Make sure player is in the same world as the receiver
					if(receiver.world.isBlockLoaded(receiver.getPos())) {
						receiver.createProxyEvent(player, commands);
					} else {
						i.remove();
					}
				}
			}
		}
	}

	public static void broadcastClaimEvents(EntityPlayer player, BlockPos pos, int dimension, boolean set) {
		Iterator<TileRemoteReceiver> i = connections.iterator();
		
		while(i.hasNext()) {
			TileRemoteReceiver receiver = i.next();
			if(receiver.isInBounds(pos)) {
				if(receiver.isInterdimensional || dimension == receiver.world.provider.getDimension()) { //Make sure player is in the same world as the receiver
					if(receiver.world.isBlockLoaded(receiver.getPos())) {
						receiver.createClaimEvent(player, pos, dimension, set);
					} else {
						i.remove();
					}
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

		if(isInterdimensional) {
			blockPosMap.put("dim", player.world.provider.getDimension());
		}

		Integer faceNum = face != null ? face.ordinal() : null;

		Object arguments[] = { player.getName(), remoteId, hitPosMap, blockPosMap, faceNum };
		for( IComputerAccess comp : computers) {
			comp.queueEvent("remote_control", arguments);
		}
	}

	public void createTouchMapEvent(EntityPlayer player, ItemStack heldItem, Vec3d hitPos, BlockPos blockPos, EnumFacing face) {
		Map<String, Double> hitPosMap = new HashMap<>();
		hitPosMap.put("x", hitPos.x);
		hitPosMap.put("y", hitPos.y);
		hitPosMap.put("z", hitPos.z);

		Map<String, Integer> blockPosMap = new HashMap<>();
		blockPosMap.put("x", blockPos.getX());
		blockPosMap.put("y", blockPos.getY());
		blockPosMap.put("z", blockPos.getZ());

		if(isInterdimensional) {
			blockPosMap.put("dim", player.world.provider.getDimension());
		}

		Integer faceNum = face != null ? face.ordinal() : null;

		Object arguments[] = { player.getName(), heldItem.getItem().getUnlocalizedName(), hitPosMap, blockPosMap, faceNum };
		for( IComputerAccess comp : computers) {
			comp.queueEvent("touch_map", arguments);
		}
	}
	
	public void createProxyEvent(EntityPlayer player, String[] command) {

		BlockPos blockPos = player.getPosition();

		Map<String, Integer> blockPosMap = new HashMap<>();
		blockPosMap.put("x", blockPos.getX());
		blockPosMap.put("y", blockPos.getY());
		blockPosMap.put("z", blockPos.getZ());

		if(isInterdimensional) {
			blockPosMap.put("dim", player.world.provider.getDimension());
		}

		int dim = player.world.provider.getDimension();

		Object arguments[] = { player.getName(), blockPosMap, dim, command };
		for( IComputerAccess comp : computers) {
			comp.queueEvent(CommandProx.PROX, arguments);
		}
	}
	
	private void createClaimEvent(EntityPlayer player, BlockPos blockPos, int dimension, boolean set) {
		Map<String, Integer> blockPosMap = new HashMap<>();
		blockPosMap.put("x", blockPos.getX());
		blockPosMap.put("y", blockPos.getY());
		blockPosMap.put("z", blockPos.getZ());
		
		Object arguments[] = { player != null ? player.getName() : null, blockPosMap, dimension, set };
		for( IComputerAccess comp : computers) {
			comp.queueEvent("claim", arguments);
		}	
	}
	
	public enum ComputerMethod implements MethodDescriptorProvider {
		connect("", "", (world, peri, args) -> obj(getTool(peri).connect())),
		disconnect("", "", (world, peri, args) -> obj(getTool(peri).disconnect())),
		setInterdimensional("b", "value", (world, peri, args) -> obj(getTool(peri).setInterdimensional(args.b()))),
		addCuboidBounds("nnnnnn", "minX,minY,minZ,maxX,maxY,maxZ",
				(world, peri, args) -> obj(getTool(peri).setBounds(args.i(), args.i(), args.i(), args.i(), args.i(), args.i()))),
		clearBounds("","", (world, peri, args) -> obj(getTool(peri).clearBounds())),
		touchButton("nnn","x,y,z", (world, peri, args) -> obj(getTool(peri).touchButton(args.i(), args.i(), args.i())));

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
		return connections.size();
	}

	public int disconnect() {
		connections.remove(this);
		return connections.size();
	}

	public int setInterdimensional(boolean isInterdimensional) {
		this.isInterdimensional = isInterdimensional;
		return 0;
	}

	public int setBounds(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
		bounds = new BoundsCuboid(Arrays.asList(new BlockPos(minX, minY, minZ), new BlockPos(maxX, maxY, maxZ)));
		return 0;
	}

	public int clearBounds() {
		bounds = null;
		return 0;
	}

	public int touchButton(int x, int y, int z) {
		BlockPos pos = new BlockPos(x, y, z);
		IBlockState state = world.getBlockState(pos);
		Block block = state.getBlock();
		if(block instanceof BlockTouchButton) {
			BlockTouchButton touchButton = (BlockTouchButton) block;
			touchButton.buttonActivate(world, pos, state);
		}
		return 0;
	}
	
	public boolean isInBounds(BlockPos pos) {
		return bounds == null || bounds.inBounds(pos);
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

	@Override
	public int hashCode() {
		return this.getPos().hashCode() ^ (world.provider.getDimension() * 7933711);
	}

}
