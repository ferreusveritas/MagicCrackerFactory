package com.ferreusveritas.mcf.event;

import com.ferreusveritas.mcf.MCF;
import com.ferreusveritas.mcf.block.ActivatableRemote;
import com.ferreusveritas.mcf.peripheral.RemoteReceiverPeripheral;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = MCF.MOD_ID)
public class RemoteClickHandler {

    @SubscribeEvent
    public static void onRemoteClickEvent(RemoteClickEvent event) {
        Player player = event.getPlayer();
        Level level = event.getPlayer().level;
        BlockPos blockPos = event.getBlockPos();
        Vec3 hitPos = event.getHitPos();
        Direction sideHit = event.getSideHit();

        if (!level.isClientSide) {
            RemoteReceiverPeripheral.broadcastRemoteEvents(player, event.getRemoteId(), hitPos, blockPos, sideHit);

            BlockState state = level.getBlockState(blockPos);
            Block block = state.getBlock();
            if (block instanceof ActivatableRemote remote) {
                remote.activate(
                        level, blockPos, state, player, new BlockHitResult(hitPos, sideHit, blockPos, false)
                );
            }

        }
    }

}
