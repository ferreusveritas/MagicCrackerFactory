package com.ferreusveritas.mcf.event;

import com.ferreusveritas.mcf.MCF;
import com.ferreusveritas.mcf.peripheral.RemoteReceiverPeripheral;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = MCF.MOD_ID)
public class TouchMapHandler {

    @SubscribeEvent
    public static void onTouchMapEvent(TouchMapEvent event) {
        PlayerEntity player = event.getPlayer();
        World world = player.level;
        BlockPos pos = event.getBlockPos();
        Vector3d hitPos = event.getHitPos();
        Direction side = event.getSideHit();
        ItemStack heldItem = event.getHeldItem();

        if (!world.isClientSide) {
            RemoteReceiverPeripheral.broadcastTouchMapEvents(player, heldItem, hitPos, pos, side);
        }
    }

}
