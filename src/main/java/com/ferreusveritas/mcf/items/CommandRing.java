package com.ferreusveritas.mcf.items;

import java.util.function.Predicate;

import com.ferreusveritas.mcf.ModTabs;
import com.ferreusveritas.mcf.tileentity.TileRemoteReceiver;

import baubles.api.BaubleType;
import baubles.api.BaublesApi;
import baubles.api.IBauble;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.Constants.NBT;

public abstract class CommandRing extends CommandItem implements IBauble {
	
	public CommandRing(String name, String displayName) {
		super(displayName);
		setRegistryName(name);
		setUnlocalizedName(getRegistryName().toString());
		setCreativeTab(ModTabs.mcfTab);
	}
	
	@Override
	public BaubleType getBaubleType(ItemStack arg0) {
		return BaubleType.RING;
	}
	
	@Override
	public void onEquipped(ItemStack itemstack, EntityLivingBase entityLiving) {
		IBauble.super.onEquipped(itemstack, entityLiving);
		if(!entityLiving.world.isRemote && entityLiving instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) entityLiving;
			satisfyWorn(player);
			updateRing(itemstack, player);
		}
	}
	
	@Override
	public void onWornTick(ItemStack itemstack, EntityLivingBase entityLiving) {
		if(!entityLiving.world.isRemote && entityLiving instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) entityLiving;
			if(shouldRun(player)) {
				processWorn(player);
				satisfyWorn(player);
			}
		}
	}
	
	public abstract boolean shouldRun(EntityPlayer player);
	
	public abstract void processWorn(EntityPlayer player);
	
	public abstract void satisfyWorn(EntityPlayer player);
	
	public void updateRing(ItemStack ring, EntityPlayer player) {
		if(ring.hasTagCompound()) {
			NBTTagCompound nbt = ring.getTagCompound();
			if(nbt.hasKey("command", NBT.TAG_STRING)) {
				String command = nbt.getString("command");
				if(!command.isEmpty()) {
					if(player instanceof EntityPlayer) {
						TileRemoteReceiver.broadcastRingEvents((EntityPlayer) player, command);
					}
				}
			}
		}
	}
	
	public void updateAllRings(EntityPlayer player, Predicate<ItemStack> predicate) {
		@SuppressWarnings("deprecation")
		IInventory inventory = BaublesApi.getBaubles(player);
		int invSize = inventory.getSizeInventory();
		
		for(int i = 0; i < invSize; i++) {
			ItemStack baubleItemStack = inventory.getStackInSlot(i);
			if(predicate.test(baubleItemStack)) {
				updateRing(baubleItemStack, player);
			}
		}
	}
	
}
