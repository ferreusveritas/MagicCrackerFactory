package com.ferreusveritas.mcf.items;

import java.awt.Color;
import java.util.List;

import javax.annotation.Nullable;

import com.ferreusveritas.mcf.ModTabs;

import baubles.api.BaubleType;
import baubles.api.IBauble;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class CommandRing extends Item implements IBauble {
	
	public CommandRing(String name) {
		setRegistryName(name);
		setUnlocalizedName(getRegistryName().toString());
		setCreativeTab(ModTabs.mcfTab);
	}
	
	@Override
	public BaubleType getBaubleType(ItemStack arg0) {
		return BaubleType.RING;
	}
	
	public NBTTagCompound getNBT(ItemStack itemStack) {
		return itemStack.hasTagCompound() ? itemStack.getTagCompound() : new NBTTagCompound();
	}
	
	@Override
	public abstract void onWornTick(ItemStack itemstack, EntityLivingBase player);
	
	public int getColor(ItemStack itemStack, int tintIndex) {
		if(tintIndex == 0) {
			NBTTagCompound nbt = getNBT(itemStack);
			int color = 0xFF00FFFF;// 0xAARRGGBB
			
			if(nbt.hasKey("color", NBT.TAG_STRING)) {
				try {
					color = Color.decode(nbt.getString("color")).getRGB();
				} catch (NumberFormatException e) {
					nbt.removeTag("color");
				}
			}
			
			return color;
		}
		
		return 0xFFFFFFFF;//White
	}
	
	public CommandRing setColor(ItemStack itemStack, String colStr) {
		NBTTagCompound nbt = getNBT(itemStack);
		nbt.setString("color", colStr);
		itemStack.setTagCompound(nbt);
		return this;
	}
	
	public String getItemStackDisplayName(ItemStack stack) {
		NBTTagCompound nbt = getNBT(stack);
		
		if(nbt.hasKey("label", NBT.TAG_STRING)) {
			return nbt.getString("label");
		}
		
		return "Command Ring";
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
