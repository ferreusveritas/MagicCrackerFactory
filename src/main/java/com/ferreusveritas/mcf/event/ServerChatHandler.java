package com.ferreusveritas.mcf.event;

import com.ferreusveritas.mcf.tileentity.TileRemoteReceiver;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
public class ServerChatHandler {
	
	@SubscribeEvent
	public static void onServerChatEvent(ServerChatEvent event) {
		EntityPlayerMP player = event.getPlayer();
		World world = player.getEntityWorld();
		String msg = event.getMessage();
		
		if(!world.isRemote) {
			TileRemoteReceiver.broadcastChatEvents(player, msg);
		}
	}
	
}
