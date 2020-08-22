package com.ferreusveritas.mcf.event;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.event.entity.player.PlayerEvent;

public class TouchMapEvent extends PlayerEvent {
	
	protected final Vec3d hitPos;
	protected final BlockPos blockPos;
	protected final EnumFacing sideHit;
	protected final ItemStack heldItem;
	
	public TouchMapEvent(EntityPlayer player, ItemStack heldItem, Vec3d clickPos, BlockPos blockPos, EnumFacing sideHit) {
		super(player);
		this.heldItem = heldItem;
		this.hitPos = clickPos;
		this.blockPos = blockPos;
		this.sideHit = sideHit;
	}
	
	public ItemStack getHeldItem() {
		return heldItem;
	}
	
	public Vec3d getHitPos() {
		return hitPos;
	}
	
	public BlockPos getBlockPos() {
		return blockPos;
	}
	
	public EnumFacing getSideHit() {
		return sideHit;
	}
	
}
