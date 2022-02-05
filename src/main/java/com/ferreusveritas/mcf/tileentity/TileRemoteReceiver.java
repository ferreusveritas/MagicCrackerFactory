package com.ferreusveritas.mcf.tileentity;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

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
	
	private BoundsCuboid bounds = null;
	
	public TileRemoteReceiver() {
		super(REMOTERECEIVER);
	}
	
	public static void broadcastEvents(EntityPlayer player, BlockPos blockPos, Consumer<TileRemoteReceiver> consumer) {
		Iterator<TileRemoteReceiver> i = connections.iterator();//Must use iterator in order to remove in loop
		
		while(i.hasNext()) {
			TileRemoteReceiver receiver = i.next();
			if(receiver.isInBounds(blockPos)) {
				if(player.world.provider.getDimension() == receiver.world.provider.getDimension()) { //Make sure player is in the same world as the receiver
					if(receiver.world.isBlockLoaded(receiver.getPos())) {
						consumer.accept(receiver);
					} else {
						i.remove();
					}
				}
			}
		}
	}
	
	public static void broadcastRemoteEvents(EntityPlayer player, String remoteId, Vec3d hitPos, BlockPos blockPos, EnumFacing face) {
		broadcastEvents(player, blockPos, receiver -> receiver.createRemoteEvent(player, remoteId, hitPos, blockPos, face));
	}
	
	public static void broadcastTouchMapEvents(EntityPlayer player, ItemStack heldItem, Vec3d hitPos, BlockPos blockPos, EnumFacing face) {
		broadcastEvents(player, blockPos, receiver -> receiver.createTouchMapEvent(player, heldItem, hitPos, blockPos, face));
	}
	
	public static void broadcastProxyEvents(EntityPlayer player, String[] commands) {
		broadcastEvents(player, player.getPosition(), receiver -> receiver.createProxyEvent(player, commands));
	}
	
	public static void broadcastPotionEvents(EntityPlayer player, String command) {
		broadcastEvents(player, player.getPosition(), receiver -> receiver.createPotionEvent(player, command));
	}
	
	public static void broadcastSplashEvents(EntityPlayer player, BlockPos pos, EnumFacing face, String command) {
		broadcastEvents(player, player.getPosition(), receiver -> receiver.createSplashEvent(player, pos, face, command));
	}
	
	public static void broadcastRingEvents(EntityPlayer player, String command) {
		broadcastEvents(player, player.getPosition(), receiver -> receiver.createRingEvent(player, command));
	}
	
	public static void broadcastClaimEvents(EntityPlayer player, BlockPos pos, int dimension, boolean set) {
		broadcastEvents(player, player.getPosition(), receiver -> receiver.createClaimEvent(player, pos, dimension, set));
	}
	
	public static void broadcastChatEvents(EntityPlayer player, String message) {
		broadcastEvents(player, player.getPosition(), receiver -> receiver.createChatEvent(player, message));
	}
	
	private Map<String, Integer> mapBlockPos(BlockPos blockPos) {
		return mapBlockPos(blockPos, null);
	}
	
	private Map<String, Integer> mapBlockPos(BlockPos blockPos, EnumFacing face) {
		Map<String, Integer> blockPosMap = new HashMap<>();
		blockPosMap.put("x", blockPos.getX());
		blockPosMap.put("y", blockPos.getY());
		blockPosMap.put("z", blockPos.getZ());
		if(face != null) {
			blockPosMap.put("face", face.ordinal());
		}
		return blockPosMap;
	}
	
	private Map<String, Double> mapHitPos(Vec3d hitPos) {
		Map<String, Double> hitPosMap = new HashMap<>();
		hitPosMap.put("x", hitPos.x);
		hitPosMap.put("y", hitPos.y);
		hitPosMap.put("z", hitPos.z);
		return hitPosMap;
	}
	
	private void sendEventToAllAttachedComputers(String event, Object[] arguments) {
		computers.forEach(comp -> comp.queueEvent(event, arguments));
	}
	
	public void createRemoteEvent(EntityPlayer player, String remoteId, Vec3d hitPos, BlockPos blockPos, EnumFacing face) {
		sendEventToAllAttachedComputers("remote_control",
			new Object[] { player.getName(), remoteId, mapHitPos(hitPos), mapBlockPos(blockPos), face != null ? face.ordinal() : null });
	}
	
	public void createTouchMapEvent(EntityPlayer player, ItemStack heldItem, Vec3d hitPos, BlockPos blockPos, EnumFacing face) {
		sendEventToAllAttachedComputers("touch_map", 
			new Object[] { player.getName(), heldItem.getItem().getUnlocalizedName(), mapHitPos(hitPos), mapBlockPos(blockPos), face != null ? face.ordinal() : null });
	}
	
	public void createProxyEvent(EntityPlayer player, String[] command) {
		sendEventToAllAttachedComputers(CommandProx.PROX,
			new Object[] { player.getName(), mapBlockPos(player.getPosition()), command });
	}
	
	public void createPotionEvent(EntityPlayer player, String command) {
		sendEventToAllAttachedComputers("potion", 
			new Object[] { player.getName(), mapBlockPos(player.getPosition()), command });
	}
	
	public void createSplashEvent(EntityPlayer player, BlockPos pos, EnumFacing face, String command) {
		sendEventToAllAttachedComputers("splash", 
			new Object[] { player.getName(), mapBlockPos(pos, face), command });
	}
	
	public void createRingEvent(EntityPlayer player, String command) {
		sendEventToAllAttachedComputers("ring", 
			new Object[] { player.getName(), mapBlockPos(player.getPosition()), command });
	}
	
	private void createClaimEvent(EntityPlayer player, BlockPos blockPos, int dimension, boolean set) {
		sendEventToAllAttachedComputers("claim", 
			new Object[] { player != null ? player.getName() : null, mapBlockPos(blockPos), set });
	}
	
	private void createChatEvent(EntityPlayer player, String message) {
		sendEventToAllAttachedComputers("chat", 
			new Object[] { player.getName(), mapBlockPos(player.getPosition()), message });
	}
	
	public enum ComputerMethod implements MethodDescriptorProvider {
		connect("", "", (world, peri, args) -> obj(getTool(peri).connect())),
		disconnect("", "", (world, peri, args) -> obj(getTool(peri).disconnect())),
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
