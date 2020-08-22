package com.ferreusveritas.mcf.event;

import com.ferreusveritas.mcf.tileentity.TileRemoteReceiver;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
public class TouchMapHandler {
	
	@SubscribeEvent
	public static void onTouchMapEvent(TouchMapEvent event) {
		EntityPlayer player = event.getEntityPlayer();
		World world = event.getEntityPlayer().world;
		BlockPos blockPos = event.getBlockPos();
		Vec3d hitPos = event.getHitPos();
		EnumFacing side = event.getSideHit();
		ItemStack heldItem = event.getHeldItem();
		
		if(!world.isRemote) {
			TileRemoteReceiver.broadcastTouchMapEvents(player, heldItem, hitPos, blockPos, side);
		}
	}
	
}
