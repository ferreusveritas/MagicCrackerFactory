package com.ferreusveritas.mcf.items;

import java.awt.Color;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class CommandItem extends Item {
	
	private final String displayName;
	
	public CommandItem(String displayName) {
		this.displayName = displayName;
	}
	
	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
	}
	
	public int getColor(ItemStack stack, int tintIndex) {
		if(tintIndex == 0) {
			NBTTagCompound nbt = getNBT(stack);
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
	
	public CommandItem setColor(ItemStack stack, String colStr) {
		NBTTagCompound nbt = getNBT(stack);
		nbt.setString("color", colStr);
		stack.setTagCompound(nbt);
		return this;
	}
	
	public NBTTagCompound getNBT(ItemStack stack) {
		return stack.hasTagCompound() ? stack.getTagCompound() : new NBTTagCompound();
	}
	
	@Override
	public String getItemStackDisplayName(ItemStack stack) {
		NBTTagCompound nbt = getNBT(stack);
		
		if(nbt.hasKey("label", NBT.TAG_STRING)) {
			return nbt.getString("label");
		}
		
		return unnamedDisplayName(stack);
	}
	
	public String getCommand(ItemStack commandPotion) {
		NBTTagCompound nbt = getNBT(commandPotion);
		if(nbt.hasKey("command", NBT.TAG_STRING)) {
			return nbt.getString("command");
		}
		
		return "";
	}
	
	protected String unnamedDisplayName(ItemStack stack) {
		return displayName;
	}
	
	/**
	 * allows items to add custom lines of information to the mouseover description
	 */
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		NBTTagCompound nbt = getNBT(stack);
		if(nbt.hasKey("info", NBT.TAG_STRING)) {
			String info = nbt.getString("info");
			tooltip.add(info);
		}
	}
	
}
