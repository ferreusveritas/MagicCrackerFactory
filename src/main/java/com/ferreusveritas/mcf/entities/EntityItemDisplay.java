package com.ferreusveritas.mcf.entities;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

/**
 * 
 * This Entity shows a single item in space.
 * The item doesn't move or despawn.  Similar
 * to an EntityArmorStand but less resource
 * intensive.
 * 
 * @author ferreusveritas
 *
 */
public class EntityItemDisplay extends Entity {
	
	private ItemStack stack = ItemStack.EMPTY;
	
	public EntityItemDisplay(World worldIn) {
		super(worldIn);
	}
	
	@Override
	protected void entityInit() { }
	
	public void setItemStack(ItemStack stack) {
		this.stack = stack;
	}
	
	public ItemStack getItemStack() {
		return stack;
	}
	
	@Override
	protected void readEntityFromNBT(NBTTagCompound compound) {
		NBTTagCompound itemNBT = compound.getCompoundTag("item");
		setItemStack(new ItemStack(itemNBT));
	}
	
	@Override
	protected void writeEntityToNBT(NBTTagCompound compound) {
		
		NBTTagCompound itemNBT = new NBTTagCompound();
		ItemStack stack = getItemStack();
		
        if (!stack.isEmpty()) {
        	stack.writeToNBT(itemNBT);
        }
        
        compound.setTag("item", itemNBT);
	}
	
}
