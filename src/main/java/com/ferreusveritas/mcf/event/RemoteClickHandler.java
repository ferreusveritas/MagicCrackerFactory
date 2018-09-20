package com.ferreusveritas.mcf.event;

import com.ferreusveritas.mcf.tileentity.TileRemoteReceiver;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
public class RemoteClickHandler {
	
	@SubscribeEvent
	public static void onRemoteClickEvent(RemoteClickEvent event) {
		TileRemoteReceiver.broadcastRemoteEvents(event.getEntityPlayer(), event.getRemoteId(), event.getHitPos(), event.getBlockPos(), event.getSideHit());
	}
	
}
