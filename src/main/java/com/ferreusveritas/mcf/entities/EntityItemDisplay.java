package com.ferreusveritas.mcf.entities;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.math.Rotations;
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
	
	private static final Rotations DEFAULT_ROTATION = new Rotations(0.0F, 0.0F, 0.0F);
	private static final Float DEFAULT_SCALE = new Float(1.0F);
	
	public static final DataParameter<Rotations> ROTATION = EntityDataManager.<Rotations>createKey(EntityArmorStand.class, DataSerializers.ROTATIONS);
	public static final DataParameter<Float> SCALE = EntityDataManager.<Float>createKey(EntityArmorStand.class, DataSerializers.FLOAT);
	public static final DataParameter<ItemStack> itemParameter = EntityDataManager.createKey(EntityItemDisplay.class, DataSerializers.ITEM_STACK);
	
	private Rotations rotation;
	private Float scale;
	
	public EntityItemDisplay(World worldIn) {
		super(worldIn);
		this.rotation = DEFAULT_ROTATION;
		this.scale = DEFAULT_SCALE;
	}
	
	@Override
	public void onUpdate() {
		super.onUpdate();
		setSize(1.0f, 1.0f);
		
		Rotations rotations1 = (Rotations)this.dataManager.get(ROTATION);
		
		if (!this.rotation.equals(rotations1)) {
			this.setRotation(rotations1);
		}
		
		Float scale = (Float)this.dataManager.get(SCALE);
		
		if (!this.scale.equals(scale)) {
			this.setScale(scale);
		}
		
	}
	
	@Override
	public void onEntityUpdate() { }
	
	public void setRotation(Rotations vec) {
		this.rotation = vec;
		this.dataManager.set(ROTATION, vec);
	}
	
	public Rotations getRotation() {
		return this.rotation;
	}
	
	public void setScale(float scale) {
		this.scale = scale;
		this.dataManager.set(SCALE, scale);
	}
	
	public float getScale() {
		return this.scale;
	}
	
	@Override
	protected void entityInit() { 
		getDataManager().register(itemParameter, ItemStack.EMPTY);
		this.dataManager.register(ROTATION, DEFAULT_ROTATION);
		this.dataManager.register(SCALE, DEFAULT_SCALE);
	}
	
	public void setItemStack(ItemStack stack) {
		getDataManager().set(itemParameter, stack);
	}
	
	public ItemStack getItemStack() {
		return getDataManager().get(itemParameter);
	}
	
	@Override
	protected void readEntityFromNBT(NBTTagCompound compound) {
		// /summon mcf:item_display 0.5 130 5.5 {item:{id:"minecraft:redstone",Count:1b},scale:4.0f}
		// /summon mcf:item_display ~ ~ ~ {item:{id:"minecraft:redstone",Count:1b}}
		// /kill @e[type=!Player]
		
		NBTTagCompound itemNBT = compound.getCompoundTag("item");
		setItemStack(new ItemStack(itemNBT));
		
		NBTTagList nbttaglist1 = compound.getTagList("rotation", 5);
		this.setRotation(nbttaglist1.hasNoTags() ? DEFAULT_ROTATION : new Rotations(nbttaglist1));
		
		Float scale = compound.getFloat("scale");
		this.setScale(scale == 0.0f ? 1.0f : scale);
	}
	
	@Override
	protected void writeEntityToNBT(NBTTagCompound compound) {
		
		NBTTagCompound itemNBT = new NBTTagCompound();
		ItemStack stack = getItemStack();
		
		if (!stack.isEmpty()) {
			stack.writeToNBT(itemNBT);
		}
		
		compound.setTag("item", itemNBT);
		
		if (!DEFAULT_ROTATION.equals(this.rotation)) {
			compound.setTag("rotation", this.rotation.writeToNBT());
		}
		
		if (!DEFAULT_SCALE.equals(this.scale)) {
			compound.setFloat("scale", this.scale);
		}
		
	}
	
}
