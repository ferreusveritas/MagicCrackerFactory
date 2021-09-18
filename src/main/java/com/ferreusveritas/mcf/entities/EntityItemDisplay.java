package com.ferreusveritas.mcf.entities;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.math.AxisAlignedBB;
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

	public static final DataParameter<Rotations> ROTATION = EntityDataManager.<Rotations>createKey(EntityItemDisplay.class, DataSerializers.ROTATIONS);
	public static final DataParameter<Float> SCALE = EntityDataManager.<Float>createKey(EntityItemDisplay.class, DataSerializers.FLOAT);
	public static final DataParameter<ItemStack> itemParameter = EntityDataManager.createKey(EntityItemDisplay.class, DataSerializers.ITEM_STACK);

	private Rotations rotation;
	private Float scale;

	public EntityItemDisplay(World worldIn) {
		super(worldIn);
		rotation = DEFAULT_ROTATION;
		scale = DEFAULT_SCALE;
	}

	@Override
	public void onUpdate() {
		super.onUpdate();

		Rotations rotations1 = (Rotations)dataManager.get(ROTATION);

		if (!rotation.equals(rotations1)) {
			setRotation(rotations1);
		}

		Float scale = (Float)dataManager.get(SCALE);

		if (!this.scale.equals(scale)) {
			setScale(scale);
		}

		setSize(this.scale, this.scale);	

		//setEntityBoundingBox(new AxisAlignedBB(posX - 0.5, posY - 0.5, posZ - 0.5D, posX + 0.5, posY + 0.5, posZ + 0.5));
	}

	@Override
	protected void setSize(float width, float height) {
		if (width != this.width || height != this.height) {
			this.width = width;
			this.height = height;
			float w = width / 2.0f;
			float h = height / 2.0f;
			setEntityBoundingBox(new AxisAlignedBB(posX - w, posY - h, posZ - w, posX + w, posY + h, posZ + w));
		}
	}

	@Override
	public float getEyeHeight() {
		return 0.0f;
	}
	
	@Override
	public void setPosition(double x, double y, double z) {
		this.posX = x;
		this.posY = y;
		this.posZ = z;
		if (isAddedToWorld() && !world.isRemote) world.updateEntityWithOptionalForce(this, false); // Forge - Process chunk registration after moving.
		double w = this.width / 2.0f;
		double h = this.height / 2.0f;
		setEntityBoundingBox(new AxisAlignedBB(x - w, y - h, z - w, x + w, y + h, z + w));
	}

	
	@Override
	public void onEntityUpdate() { }

	public void setRotation(Rotations vec) {
		rotation = vec;
		dataManager.set(ROTATION, vec);
	}

	public Rotations getRotation() {
		return rotation;
	}

	public void setScale(float scale) {
		this.scale = scale;
		dataManager.set(SCALE, scale);
	}

	public float getScale() {
		return scale;
	}

	@Override
	protected void entityInit() { 
		getDataManager().register(itemParameter, ItemStack.EMPTY);
		dataManager.register(ROTATION, DEFAULT_ROTATION);
		dataManager.register(SCALE, DEFAULT_SCALE);
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
		// /summon mcf:item_display ~5 ~1 ~ {item:{id:"thermalexpansion:frame",Count:1b},scale:4.0f,rotation:[35f,0f,45f]}
		// /summon mcf:item_display ~ ~ ~ {item:{id:"minecraft:redstone",Count:1b}}
		// /summon mcf:item_display 2608 65.5 27 {item:{id:"cathedral:cathedral_gargoyle_demon_stone",Count:1b},scale:4.0f,rotation:[0f,0f,0f]}
		// /kill @e[type=mcf:item_display]

		NBTTagCompound itemNBT = compound.getCompoundTag("item");
		setItemStack(new ItemStack(itemNBT));

		NBTTagList nbttaglist1 = compound.getTagList("rotation", 5);
		setRotation(nbttaglist1.hasNoTags() ? DEFAULT_ROTATION : new Rotations(nbttaglist1));

		Float scale = compound.getFloat("scale");
		setScale(scale == 0.0f ? DEFAULT_SCALE : scale);
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound compound) {

		NBTTagCompound itemNBT = new NBTTagCompound();
		ItemStack stack = getItemStack();

		if (!stack.isEmpty()) {
			stack.writeToNBT(itemNBT);
		}

		compound.setTag("item", itemNBT);

		if (!DEFAULT_ROTATION.equals(rotation)) {
			compound.setTag("rotation", rotation.writeToNBT());
		}

		if (!DEFAULT_SCALE.equals(scale)) {
			compound.setFloat("scale", scale);
		}

	}

}
