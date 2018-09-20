package com.ferreusveritas.mcf.items;

import com.ferreusveritas.mcf.MCF;
import com.ferreusveritas.mcf.network.PacketRemoteClick;
import com.ferreusveritas.mcf.util.Util;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class UniversalRemote extends Item {
	
	public UniversalRemote() {
		setRegistryName("remote");
		setUnlocalizedName(getRegistryName().toString());
		setCreativeTab(Util.findCreativeTab("ComputerCraft"));
	}
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand handIn) {
		
		if(world.isRemote) {
			ItemStack remoteStack = player.getHeldItem(handIn);
			
			double range = getRange(remoteStack);
			
			RayTraceResult rtr = player.rayTrace(range, 0);
			if(rtr != null && rtr.typeOfHit == Type.BLOCK) {
				Vec3d hitPos = rtr.hitVec;
				BlockPos blockPos = rtr.getBlockPos();
				EnumFacing sideHit = rtr.sideHit;
				sendPacketToServer(hitPos, blockPos, sideHit);
			}
		}
		
		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, player.getHeldItem(handIn));
	}
	
	private double getRange(ItemStack remoteStack) {
		return 32;
	}
	
	public String getRemoteId(ItemStack remoteStack) {
		if(remoteStack.hasTagCompound()) {
			NBTTagCompound tag = remoteStack.getTagCompound();
			return tag.getString("remoteId");
		} else {
			return "";
		}
	}
	
	private void sendPacketToServer(Vec3d hitPos, BlockPos blockPos, EnumFacing sideHit) {
		PacketRemoteClick remoteClickPacket = new PacketRemoteClick(hitPos, blockPos, sideHit);
		MCF.network.sendToServer(remoteClickPacket);
	}
	
}