package com.ferreusveritas.mcf.items;

import com.ferreusveritas.mcf.ModTabs;
import com.ferreusveritas.mcf.entities.EntityCommandPotion;
import com.ferreusveritas.mcf.tileentity.TileRemoteReceiver;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CommandSplashPotion extends CommandItem {
	
	public CommandSplashPotion() {
		super("Command splash Potion");
		setRegistryName("commandsplashpotion");
		setUnlocalizedName(getRegistryName().toString());
		setCreativeTab(ModTabs.mcfTab);
	}
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer playerIn, EnumHand handIn) {
		ItemStack itemstack = playerIn.getHeldItem(handIn);
		ItemStack itemstack1 = playerIn.capabilities.isCreativeMode ? itemstack.copy() : itemstack.splitStack(1);
		world.playSound((EntityPlayer)null, playerIn.posX, playerIn.posY, playerIn.posZ, SoundEvents.ENTITY_SPLASH_POTION_THROW, SoundCategory.PLAYERS, 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
		
		if (!world.isRemote) {
			EntityPotion entitypotion = new EntityCommandPotion(world, playerIn, itemstack1);
			entitypotion.shoot(playerIn, playerIn.rotationPitch, playerIn.rotationYaw, -20.0F, 0.5F, 1.0F);
			world.spawnEntity(entitypotion);
		}
		
		playerIn.addStat(StatList.getObjectUseStats(this));
		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemstack);
	}
	
	public void OnImpact(ItemStack stack, EntityPlayer player, BlockPos blockPos, EnumFacing facing) {
		TileRemoteReceiver.broadcastSplashEvents(player, blockPos, facing, getCommand(stack));
	}
	
}
