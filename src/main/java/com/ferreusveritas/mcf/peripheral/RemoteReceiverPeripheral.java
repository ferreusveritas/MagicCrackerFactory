package com.ferreusveritas.mcf.peripheral;

import com.ferreusveritas.mcf.Registry;
import com.ferreusveritas.mcf.block.TouchButtonBlock;
import com.ferreusveritas.mcf.block.entity.RemoteReceiverBlockEntity;
import com.ferreusveritas.mcf.command.ProxCommand;
import com.ferreusveritas.mcf.util.bounds.CuboidBounds;
import com.mojang.math.Vector3d;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IComputerAccess;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.*;
import java.util.function.Consumer;

public class RemoteReceiverPeripheral extends MCFPeripheral<RemoteReceiverBlockEntity> {

    private static final Set<RemoteReceiverPeripheral> CONNECTIONS = new HashSet<>();
    private final Set<IComputerAccess> computers = new HashSet<>();

    private CuboidBounds bounds = null;

    public RemoteReceiverPeripheral(RemoteReceiverBlockEntity block) {
        super(block);
    }

    public static void broadcastEvents(Player player, BlockPos blockPos, Consumer<RemoteReceiverPeripheral> consumer) {
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

    public static void broadcastRemoteEvents(Player player, String remoteId, Vec3 hitPos, BlockPos blockPos, Direction face) {
        broadcastEvents(player, blockPos, receiver -> receiver.createRemoteEvent(player, remoteId, hitPos, blockPos, face));
    }

    public static void broadcastTouchMapEvents(Player player, ItemStack heldItem, Vec3 hitPos, BlockPos blockPos, Direction sideHit) {
        broadcastEvents(player, blockPos, receiver -> receiver.createTouchMapEvent(player, heldItem, hitPos, blockPos, sideHit));
    }

    public static void broadcastProxyEvents(Player player, String[] commands) {
        broadcastEvents(player, player.blockPosition(), receiver -> receiver.createProxyEvent(player, commands));
    }

    public static void broadcastPotionEvents(Player player, String command) {
        broadcastEvents(player, player.blockPosition(), receiver -> receiver.createPotionEvent(player, command));
    }

    public static void broadcastSplashEvents(Player player, BlockPos pos, Direction face, String command) {
        broadcastEvents(player, player.blockPosition(), receiver -> receiver.createSplashEvent(player, pos, face, command));
    }

    public static void broadcastRingEvents(Player player, String command) {
        broadcastEvents(player, player.blockPosition(), receiver -> receiver.createRingEvent(player, command));
    }

    public static void broadcastClaimEvents(Player player, BlockPos pos, boolean set) {
        broadcastEvents(player, player.blockPosition(), receiver -> receiver.createClaimEvent(player, pos, set));
    }

    public static void broadcastChatEvents(Player player, String message) {
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

    private Map<String, Double> mapHitPos(Vec3 hitPos) {
        Map<String, Double> hitPosMap = new HashMap<>();
        hitPosMap.put("x", hitPos.x);
        hitPosMap.put("y", hitPos.y);
        hitPosMap.put("z", hitPos.z);
        return hitPosMap;
    }

    private void sendEventToAllAttachedComputers(String event, Object[] arguments) {
        computers.forEach(comp -> comp.queueEvent(event, arguments));
    }

    public void createRemoteEvent(Player player, String remoteId, Vec3 hitPos, BlockPos blockPos, Direction hitSide) {
        sendEventToAllAttachedComputers("remote_control", new Object[]{player.getName().getString(), remoteId, mapHitPos(hitPos), mapBlockPos(blockPos), hitSide != null ? hitSide.ordinal() : null});
    }

    public void createTouchMapEvent(Player player, ItemStack heldItem, Vec3 hitPos, BlockPos blockPos, Direction hitSide) {
        sendEventToAllAttachedComputers("touch_map", new Object[]{player.getName().getString(), heldItem.getItem().getDescriptionId(), mapHitPos(hitPos), mapBlockPos(blockPos), hitSide != null ? hitSide.ordinal() : null});
    }

    public void createProxyEvent(Player player, String[] command) {
        sendEventToAllAttachedComputers(ProxCommand.PROX, new Object[]{player.getName().getString(), mapBlockPos(player.blockPosition()), command});
    }

    public void createPotionEvent(Player player, String command) {
        sendEventToAllAttachedComputers("potion", new Object[]{player.getName().getString(), mapBlockPos(player.blockPosition()), command});
    }

    public void createSplashEvent(Player player, BlockPos pos, Direction face, String command) {
        sendEventToAllAttachedComputers("splash", new Object[]{player.getName().getString(), mapBlockPos(pos, face), command});
    }

    public void createRingEvent(Player player, String command) {
        sendEventToAllAttachedComputers("ring", new Object[]{player.getName().getString(), mapBlockPos(player.blockPosition()), command});
    }

    private void createClaimEvent(Player player, BlockPos blockPos, boolean set) {
        sendEventToAllAttachedComputers("claim", new Object[]{player != null ? player.getName().getString() : null, mapBlockPos(blockPos), set});
    }

    private void createChatEvent(Player player, String message) {
        sendEventToAllAttachedComputers("chat", new Object[]{player.getName().getString(), mapBlockPos(player.blockPosition()), message});
    }

    @LuaFunction
    public final int connect() {
        CONNECTIONS.add(this);
        return CONNECTIONS.size();
    }

    @LuaFunction
    public final int disconnect() {
        CONNECTIONS.remove(this);
        return CONNECTIONS.size();
    }

    @LuaFunction
    public final int setBounds(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
        bounds = new CuboidBounds(Arrays.asList(new BlockPos(minX, minY, minZ), new BlockPos(maxX, maxY, maxZ)));
        return 0;
    }

    @LuaFunction
    public final int clearBounds() {
        bounds = null;
        return 0;
    }

    @LuaFunction
    public final int touchButton(int x, int y, int z) {
        BlockPos pos = new BlockPos(x, y, z);
        BlockState state = block.getLevel().getBlockState(pos);
        Block block = state.getBlock();
        if (block instanceof TouchButtonBlock touchButton) {
            touchButton.touchPress(this.block.getLevel(), pos, state);
            return 0;
        }
        return 1;
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
