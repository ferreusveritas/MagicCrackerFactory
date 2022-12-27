package com.ferreusveritas.mcf.event;

import com.ferreusveritas.mcf.block.ActivatableRemote;
import com.ferreusveritas.mcf.peripheral.RemoteReceiverPeripheral;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class RemoteClickHandler {

    @SubscribeEvent
    public static void onRemoteClickEvent(RemoteClickEvent event) {
        PlayerEntity player = event.getPlayer();
        World world = event.getPlayer().level;
        BlockPos blockPos = event.getBlockPos();
        Vector3d hitPos = event.getHitPos();
        Direction side = event.getSideHit();

        if (!world.isClientSide) {
            RemoteReceiverPeripheral.broadcastRemoteEvents(player, event.getRemoteId(), hitPos, blockPos, side);

            BlockState state = world.getBlockState(blockPos);
            Block block = state.getBlock();
            if (block instanceof ActivatableRemote) {
                ((ActivatableRemote) block).activate(
                        world, blockPos, state, player, new BlockRayTraceResult(hitPos, side, blockPos, false)
                );
            }

        }
    }

}
