package com.ferreusveritas.mcf.items;

import java.awt.Color;
import java.util.List;

import javax.annotation.Nullable;

import com.ferreusveritas.mcf.MCF;
import com.ferreusveritas.mcf.ModTabs;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class CommandPotion extends ItemPotion {
	
	public CommandPotion() {
		setRegistryName("commandpotion");
		setUnlocalizedName(getRegistryName().toString());
		setCreativeTab(ModTabs.mcfTab);
	}
	
	@Override
	public ItemStack onItemUseFinish(ItemStack itemstack, World world, EntityLivingBase entityLiving) {
		
		if(entityLiving instanceof EntityPlayer) {
			
			EntityPlayer entityplayer = (EntityPlayer)entityLiving;
			
			if(world.isRemote) {
				
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
			
			if (!entityplayer.capabilities.isCreativeMode) {
				itemstack.shrink(1);
			}
			
			if (itemstack.isEmpty()) {
				return new ItemStack(Items.GLASS_BOTTLE);
			}
			
		}
		
		return itemstack;
	}
	
	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
		if (this.isInCreativeTab(tab)) {
			items.add(new ItemStack(this));
			
			ItemStack dawnPotion = new ItemStack(this);
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setString("label", "Dawn Potion(Example)");
			nbt.setString("command", "/time set 0");
			nbt.setString("color", "#FFFF20");
			nbt.setString("info", "§3Sets the current world time to morning§r");
			dawnPotion.setTagCompound(nbt);
			items.add(dawnPotion);
			
			ItemStack duskPotion = new ItemStack(this);
			nbt = new NBTTagCompound();
			nbt.setString("label", "Dusk Potion(Example)");
			nbt.setString("command", "/time set night");
			nbt.setString("color", "#000020");
			nbt.setString("info", "§3Sets the current world time to night§r");
			duskPotion.setTagCompound(nbt);
			items.add(duskPotion);
			
			ItemStack clearPotion = new ItemStack(this);
			nbt = new NBTTagCompound();
			nbt.setString("label", "Clear Weather Potion(Example)");
			nbt.setString("command", "/weather clear");
			nbt.setString("color", "#6060F0");
			nbt.setString("info", "§3Clears the current weather conditions§r");
			clearPotion.setTagCompound(nbt);
			items.add(clearPotion);
			
		}
	}
	
	public String getItemStackDisplayName(ItemStack stack) {
		NBTTagCompound nbt = getNBT(stack);
		
		if(nbt.hasKey("label", NBT.TAG_STRING)) {
			return nbt.getString("label");
		}
		
		return "Command Potion";
	}
	
	public NBTTagCompound getNBT(ItemStack itemStack) {
		return itemStack.hasTagCompound() ? itemStack.getTagCompound() : new NBTTagCompound();
	}
	
	public int getColor(ItemStack itemStack, int tintIndex) {
		
		if(tintIndex != 0) {
			return 0xFFFFFFFF;
		}
		
		NBTTagCompound nbt = getNBT(itemStack);
		
		int color = 0xFF00FFFF;
		
		if(nbt.hasKey("color", NBT.TAG_STRING)) {
			try {
				color = Color.decode(nbt.getString("color")).getRGB();
			} catch (NumberFormatException e) {
				nbt.removeTag("color");
			}
		}
		
		return color;
	}
	
	public CommandPotion setColor(ItemStack itemStack, String colStr) {
		NBTTagCompound nbt = getNBT(itemStack);
		nbt.setString("color", colStr);
		itemStack.setTagCompound(nbt);
		return this;
	}
	
	/**
	 * allows items to add custom lines of information to the mouseover description
	 */
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		NBTTagCompound nbt = getNBT(stack);
		if(nbt.hasKey("info", NBT.TAG_STRING)) {
			String info = nbt.getString("info");
			tooltip.add(info);
		}
	}
	
}
