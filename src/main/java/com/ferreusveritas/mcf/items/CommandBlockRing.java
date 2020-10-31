package com.ferreusveritas.mcf.items;

import com.ferreusveritas.mcf.MCF;

import baubles.api.BaubleType;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.Constants.NBT;

public class CommandBlockRing extends CommandRing {
	
	BlockPos lastBlockPos = null;
	
	public CommandBlockRing() {
		super("commandblockring");
	}
	
	@Override
	public BaubleType getBaubleType(ItemStack arg0) {
		return BaubleType.RING;
	}
	
	public NBTTagCompound getNBT(ItemStack itemStack) {
		return itemStack.hasTagCompound() ? itemStack.getTagCompound() : new NBTTagCompound();
	}
	
	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
		if (this.isInCreativeTab(tab)) {
			items.add(new ItemStack(this));
			
			ItemStack stoneDissolverRing = new ItemStack(this);
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setString("label", "Stone Dissolver Ring(Example)");
			nbt.setString("command", "/fill ~-3 ~-3 ~-3 ~3 ~3 ~3 air 0 replace stone 0");
			nbt.setString("color", "#555555");
			nbt.setString("info", "§3Disolves surrounding stone as player moves.§r");
			stoneDissolverRing.setTagCompound(nbt);
			items.add(stoneDissolverRing);
			
			ItemStack deathRing = new ItemStack(this);
			nbt = new NBTTagCompound();
			nbt.setString("label", "Death Ring(Example)");
			nbt.setString("command", "/kill @e[type=!Player,r=8]");
			nbt.setString("color", "#771506");
			nbt.setString("info", "§3Kills any nearby entities as player moves.§r");
			deathRing.setTagCompound(nbt);
			items.add(deathRing);
			
		}
	}
	
	@Override
	public void onWornTick(ItemStack itemstack, EntityLivingBase player) {
		
		if(player.world.isRemote) { //Commands are executed client side
			BlockPos blockPos = player.getPosition();
			
			if(!blockPos.equals(lastBlockPos)) {
				lastBlockPos = blockPos;
				
				String command = "";
				
				if(itemstack.hasTagCompound()) {
					NBTTagCompound nbt = itemstack.getTagCompound();
					if(nbt.hasKey("command", NBT.TAG_STRING)) {
						command = nbt.getString("command");
					}
				}
				
				if(!command.isEmpty()) {
					MCF.proxy.sendChatMessage(command, false);
				}
			}
		}
	}
	
}
