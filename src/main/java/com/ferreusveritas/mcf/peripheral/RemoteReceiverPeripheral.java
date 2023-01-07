package com.ferreusveritas.mcf.peripheral;

import com.ferreusveritas.mcf.Registry;
import com.ferreusveritas.mcf.block.TouchButtonBlock;
import com.ferreusveritas.mcf.command.ProxCommand;
import com.ferreusveritas.mcf.tileentity.RemoteReceiverTileEntity;
import com.ferreusveritas.mcf.util.bounds.CuboidBounds;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IComputerAccess;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;

import java.util.*;
import java.util.function.Consumer;

public class RemoteReceiverPeripheral extends MCFPeripheral<RemoteReceiverTileEntity> {

    private static final Set<RemoteReceiverPeripheral> CONNECTIONS = new HashSet<>();
    private final Set<IComputerAccess> computers = new HashSet<>();

    private CuboidBounds bounds = null;

    public RemoteReceiverPeripheral(RemoteReceiverTileEntity block) {
        super(block);
    }

    public static void broadcastEvents(PlayerEntity player, BlockPos blockPos, Consumer<RemoteReceiverPeripheral> consumer) {
        Iterator<RemoteReceiverPeripheral> i = CONNECTIONS.iterator();//Must use iterator in order to remove in loop

        while (i.hasNext()) {
            RemoteReceiverPeripheral receiver = i.next();
            if (receiver.isInBounds(blockPos)) {
                if (player.level.dimension().location().equals(player.level.dimension().location())) { //Make sure player is in the same world as the receiver
                    if (player.level.isLoaded(player.blockPosition())) {
                        consumer.accept(receiver);
                    } else {
                        i.remove();
                    }
                }
            }
        }
    }

    public static void broadcastRemoteEvents(PlayerEntity player, String remoteId, Vector3d hitPos, BlockPos blockPos, Direction face) {
        broadcastEvents(player, blockPos, receiver -> receiver.createRemoteEvent(player, remoteId, hitPos, blockPos, face));
    }

    public static void broadcastTouchMapEvents(PlayerEntity player, ItemStack heldItem, Vector3d hitPos, BlockPos blockPos, Direction face) {
        broadcastEvents(player, blockPos, receiver -> receiver.createTouchMapEvent(player, heldItem, hitPos, blockPos, face));
    }

    public static void broadcastProxyEvents(PlayerEntity player, String[] commands) {
        broadcastEvents(player, player.blockPosition(), receiver -> receiver.createProxyEvent(player, commands));
    }

    public static void broadcastPotionEvents(PlayerEntity player, String command) {
        broadcastEvents(player, player.blockPosition(), receiver -> receiver.createPotionEvent(player, command));
    }

    public static void broadcastSplashEvents(PlayerEntity player, BlockPos pos, Direction face, String command) {
        broadcastEvents(player, player.blockPosition(), receiver -> receiver.createSplashEvent(player, pos, face, command));
    }

    public static void broadcastRingEvents(PlayerEntity player, String command) {
        broadcastEvents(player, player.blockPosition(), receiver -> receiver.createRingEvent(player, command));
    }

    public static void broadcastClaimEvents(PlayerEntity player, BlockPos pos, boolean set) {
        broadcastEvents(player, player.blockPosition(), receiver -> receiver.createClaimEvent(player, pos, set));
    }

    public static void broadcastChatEvents(PlayerEntity player, String message) {
        broadcastEvents(player, player.blockPosition(), receiver -> receiver.createChatEvent(player, message));
    }

    private Map<String, Integer> mapBlockPos(BlockPos blockPos) {
        return mapBlockPos(blockPos, null);
    }

    private Map<String, Integer> mapBlockPos(BlockPos blockPos, Direction face) {
        Map<String, Integer> blockPosMap = new HashMap<>();
        blockPosMap.put("x", blockPos.getX());
        blockPosMap.put("y", blockPos.getY());
        blockPosMap.put("z", blockPos.getZ());
        if (face != null) {
            blockPosMap.put("face", face.ordinal());
        }
        return blockPosMap;
    }

    private Map<String, Double> mapHitPos(Vector3d hitPos) {
        Map<String, Double> hitPosMap = new HashMap<>();
        hitPosMap.put("x", hitPos.x);
        hitPosMap.put("y", hitPos.y);
        hitPosMap.put("z", hitPos.z);
        return hitPosMap;
    }

    private void sendEventToAllAttachedComputers(String event, Object[] arguments) {
        computers.forEach(comp -> comp.queueEvent(event, arguments));
    }

    public void createRemoteEvent(PlayerEntity player, String remoteId, Vector3d hitPos, BlockPos blockPos, Direction face) {
        sendEventToAllAttachedComputers("remote_control", new Object[]{player.getName().getString(), remoteId, mapHitPos(hitPos), mapBlockPos(blockPos), face != null ? face.ordinal() : null});
    }

    public void createTouchMapEvent(PlayerEntity player, ItemStack heldItem, Vector3d hitPos, BlockPos blockPos, Direction face) {
        sendEventToAllAttachedComputers("touch_map", new Object[]{player.getName().getString(), heldItem.getItem().getDescriptionId(), mapHitPos(hitPos), mapBlockPos(blockPos), face != null ? face.ordinal() : null});
    }

    public void createProxyEvent(PlayerEntity player, String[] command) {
        sendEventToAllAttachedComputers(ProxCommand.PROX, new Object[]{player.getName().getString(), mapBlockPos(player.blockPosition()), command});
    }

    public void createPotionEvent(PlayerEntity player, String command) {
        sendEventToAllAttachedComputers("potion", new Object[]{player.getName().getString(), mapBlockPos(player.blockPosition()), command});
    }

    public void createSplashEvent(PlayerEntity player, BlockPos pos, Direction face, String command) {
        sendEventToAllAttachedComputers("splash", new Object[]{player.getName().getString(), mapBlockPos(pos, face), command});
    }

    public void createRingEvent(PlayerEntity player, String command) {
        sendEventToAllAttachedComputers("ring", new Object[]{player.getName().getString(), mapBlockPos(player.blockPosition()), command});
    }

    private void createClaimEvent(PlayerEntity player, BlockPos blockPos, boolean set) {
        sendEventToAllAttachedComputers("claim", new Object[]{player != null ? player.getName().getString() : null, mapBlockPos(blockPos), set});
    }

    private void createChatEvent(PlayerEntity player, String message) {
        sendEventToAllAttachedComputers("chat", new Object[]{player.getName().getString(), mapBlockPos(player.blockPosition()), message});
    }

    @LuaFunction
    public int connect() {
        CONNECTIONS.add(this);
        return CONNECTIONS.size();
    }

    @LuaFunction
    public int disconnect() {
        CONNECTIONS.remove(this);
        return CONNECTIONS.size();
    }

    @LuaFunction
    public int setBounds(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
        bounds = new CuboidBounds(Arrays.asList(new BlockPos(minX, minY, minZ), new BlockPos(maxX, maxY, maxZ)));
        return 0;
    }

    @LuaFunction
    public int clearBounds() {
        bounds = null;
        return 0;
    }

    @LuaFunction
    public int touchButton(int x, int y, int z) {
        BlockPos pos = new BlockPos(x, y, z);
        BlockState state = block.getLevel().getBlockState(pos);
        Block block = state.getBlock();
        if (block instanceof TouchButtonBlock) {
            TouchButtonBlock touchButton = (TouchButtonBlock) block;
            touchButton.touchPress(this.block.getLevel(), pos, state);
        }
        return 0;
    }

    public boolean isInBounds(BlockPos pos) {
        return bounds == null || bounds.inBounds(pos);
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
    public String getType() {
        return Registry.REMOTE_RECEIVER_TILE_ENTITY.get().getRegistryName().getPath();
    }

    @Override
    public int hashCode() {
        return block.hashCode();
    }

}
