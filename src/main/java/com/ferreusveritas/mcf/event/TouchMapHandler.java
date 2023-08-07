package com.ferreusveritas.mcf.event;

import com.ferreusveritas.mcf.MCF;
import com.ferreusveritas.mcf.peripheral.RemoteReceiverPeripheral;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = MCF.MOD_ID)
public class TouchMapHandler {

    @SubscribeEvent
    public static void onTouchMapEvent(TouchMapEvent event) {
        Player player = event.getPlayer();
        Level level = player.level;
        BlockPos pos = event.getBlockPos();
        Vec3 hitPos = event.getHitPos();
        Direction sideHit = event.getSideHit();
        ItemStack heldItem = event.getHeldItem();

        if (!level.isClientSide) {
            RemoteReceiverPeripheral.broadcastTouchMapEvents(player, heldItem, hitPos, pos, sideHit);
        }
    }

}
