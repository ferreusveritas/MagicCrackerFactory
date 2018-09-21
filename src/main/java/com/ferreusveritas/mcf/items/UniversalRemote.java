package com.ferreusveritas.mcf.items;

import java.awt.Color;
import java.util.List;

import com.ferreusveritas.mcf.MCF;
import com.ferreusveritas.mcf.network.PacketRemoteClick;
import com.ferreusveritas.mcf.util.Util;

import net.minecraft.client.util.ITooltipFlag;
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
	
	public NBTTagCompound getNBT(ItemStack itemStack) {
		return itemStack.hasTagCompound() ? itemStack.getTagCompound() : new NBTTagCompound();
	}
	
	private double getRange(ItemStack remoteStack) {
		NBTTagCompound nbt = getNBT(remoteStack);
		if(nbt.hasKey("range")) {
			return nbt.getDouble("range");
		}
		
		return 16;
	}
	
	public int getColor(ItemStack itemStack) {
		NBTTagCompound nbt = getNBT(itemStack);
		
		int color = 0x0000FFFF;
		
		if(nbt.hasKey("color")) {
			try {
				color = Color.decode(nbt.getString("color")).getRGB();
			} catch (NumberFormatException e) {
				nbt.removeTag("color");
			}
		}
		
		return color;
	}
	
	public UniversalRemote setColor(ItemStack itemStack, String colStr) {
		NBTTagCompound nbt = getNBT(itemStack);
		nbt.setString("color", colStr);
		itemStack.setTagCompound(nbt);
		return this;
	}
	
	public String getRemoteId(ItemStack remoteStack) {
		NBTTagCompound nbt = getNBT(remoteStack);
		return nbt.getString("id");
	}
	
	private void sendPacketToServer(Vec3d hitPos, BlockPos blockPos, EnumFacing sideHit) {
		PacketRemoteClick remoteClickPacket = new PacketRemoteClick(hitPos, blockPos, sideHit);
		MCF.network.sendToServer(remoteClickPacket);
	}
	
	@Override
	public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag flagIn) {
		String id = getRemoteId(stack);
		tooltip.add("Id: §6" + ( id.isEmpty() ? "<none>" : id));
	}
	
}