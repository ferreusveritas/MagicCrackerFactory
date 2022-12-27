package com.ferreusveritas.mcf.event;

import com.ferreusveritas.mcf.peripheral.RemoteReceiverPeripheral;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.world.World;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class ServerChatHandler {

    @SubscribeEvent
    public static void onServerChatEvent(ServerChatEvent event) {
        ServerPlayerEntity player = event.getPlayer();
        World world = player.getCommandSenderWorld();
        String message = event.getMessage();

        if (!world.isClientSide) {
            RemoteReceiverPeripheral.broadcastChatEvents(player, message);
        }
    }

}
