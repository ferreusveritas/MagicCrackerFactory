package com.ferreusveritas.mcf.event;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.event.entity.player.PlayerEvent;

public class RemoteClickEvent extends PlayerEvent {
	
	protected final Vec3d clickPos;
	protected final ItemStack remoteItem;
	protected final BlockPos blockPos;
	protected final EnumFacing sideHit;
	
	public RemoteClickEvent(EntityPlayer player, ItemStack stack, Vec3d clickPos, BlockPos blockPos, EnumFacing sideHit) {
		super(player);
		this.clickPos = clickPos;
		this.remoteItem = stack;
		this.blockPos = blockPos;
		this.sideHit = sideHit;
	}
	
	public Vec3d getClickPos() {
		return clickPos;
	}
	
	public ItemStack getRemoteItem() {
		return remoteItem;
	}
	
	public BlockPos getBlockPos() {
		return blockPos;
	}
	
	public EnumFacing getSideHit() {
		return sideHit;
	}
	
}
