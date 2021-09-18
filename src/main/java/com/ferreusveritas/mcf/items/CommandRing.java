package com.ferreusveritas.mcf.items;

import com.ferreusveritas.mcf.ModTabs;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;

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
	public abstract void onWornTick(ItemStack itemstack, EntityLivingBase entityLiving);
	
}
