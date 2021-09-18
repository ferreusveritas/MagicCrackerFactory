package com.ferreusveritas.mcf.items;

import com.ferreusveritas.mcf.ModTabs;
import com.ferreusveritas.mcf.tileentity.TileRemoteReceiver;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class CommandPotion extends CommandItem {
	
	public CommandPotion() {
		super("Command Potion");
		setRegistryName("commandpotion");
		setUnlocalizedName(getRegistryName().toString());
		setCreativeTab(ModTabs.mcfTab);
	}
	
	@Override
	public ItemStack onItemUseFinish(ItemStack itemstack, World world, EntityLivingBase entityLiving) {
		
		if(entityLiving instanceof EntityPlayer) {
			EntityPlayer entityplayer = (EntityPlayer)entityLiving;
			
			if(!world.isRemote) {
				TileRemoteReceiver.broadcastPotionEvents(entityplayer, getCommand(itemstack));
			}
			
			if (!entityplayer.capabilities.isCreativeMode) {
				itemstack.shrink(1);
				entityplayer.inventory.addItemStackToInventory(new ItemStack(Items.GLASS_BOTTLE));
			}
			
		}
		
		return itemstack;
	}
	
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
		playerIn.setActiveHand(handIn);
		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, playerIn.getHeldItem(handIn));
	}
	
	public int getMaxItemUseDuration(ItemStack stack) {
		return 32;
	}
	
	// returns the action that specifies what animation to play when the items is being used
	public EnumAction getItemUseAction(ItemStack stack) {
		return EnumAction.DRINK;
	}
	
}
