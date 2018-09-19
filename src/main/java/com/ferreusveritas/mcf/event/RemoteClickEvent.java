package com.ferreusveritas.mcf.event;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.event.entity.player.PlayerEvent;

public class RemoteClickEvent extends PlayerEvent {
	
	protected final Vec3d clickPos;
	protected final ItemStack remoteItem;
	
	public RemoteClickEvent(EntityPlayer player, ItemStack stack, Vec3d clickPos) {
		super(player);
		this.clickPos = clickPos;
		this.remoteItem = stack;
	}
	
	public Vec3d getClickPos() {
		return clickPos;
	}
	
	public ItemStack getRemoteItem() {
		return remoteItem;
	}
	
}
