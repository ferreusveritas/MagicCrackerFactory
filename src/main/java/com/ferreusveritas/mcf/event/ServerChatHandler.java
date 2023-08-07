package com.ferreusveritas.mcf.event;

import com.ferreusveritas.mcf.MCF;
import com.ferreusveritas.mcf.peripheral.RemoteReceiverPeripheral;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = MCF.MOD_ID)
public class ServerChatHandler {

    @SubscribeEvent
    public static void onServerChatEvent(ServerChatEvent event) {
        ServerPlayer player = event.getPlayer();
        Level level = player.getCommandSenderWorld();
        String message = event.getMessage();

        if (!level.isClientSide) {
            RemoteReceiverPeripheral.broadcastChatEvents(player, message);
        }
    }

}
