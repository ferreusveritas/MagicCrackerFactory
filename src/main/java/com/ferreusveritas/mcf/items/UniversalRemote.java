package com.ferreusveritas.mcf.items;

import com.ferreusveritas.mcf.network.PacketRemoteClick;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class UniversalRemote extends Item {
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand handIn) {
		
		if(world.isRemote) {
			ItemStack remoteStack = player.getHeldItem(handIn);
			
			double range = getRange(remoteStack);
			
			RayTraceResult rtr = player.rayTrace(range, 0);
			if(rtr != null) {
				Vec3d pos = rtr.hitVec;
				sendPacketToServer(world, player, pos);
			}
		}
		
		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, player.getHeldItem(handIn));
	}
	
	private double getRange(ItemStack remoteStack) {
		return 32;
	}
	
	private void sendPacketToServer(World world, EntityPlayer player, Vec3d pos) {
		PacketRemoteClick remoteClickPacket = new PacketRemoteClick(pos);
		//TODO: Send a packet to the server
	}
	
}