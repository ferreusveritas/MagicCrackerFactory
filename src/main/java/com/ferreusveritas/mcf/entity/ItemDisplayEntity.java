package com.ferreusveritas.mcf.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Rotations;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.fml.network.NetworkHooks;

/**
 * This Entity shows a single item in space.
 * The item doesn't move or despawn.  Similar
 * to an EntityArmorStand but less resource
 * intensive.
 *
 * @author ferreusveritas
 */
public class ItemDisplayEntity extends Entity {

    public static final DataParameter<Rotations> ROTATION = EntityDataManager.defineId(ItemDisplayEntity.class, DataSerializers.ROTATIONS);
    public static final DataParameter<Float> SCALE = EntityDataManager.defineId(ItemDisplayEntity.class, DataSerializers.FLOAT);
    public static final DataParameter<ItemStack> ITEM = EntityDataManager.defineId(ItemDisplayEntity.class, DataSerializers.ITEM_STACK);
    private static final Rotations DEFAULT_ROTATION = new Rotations(0.0F, 0.0F, 0.0F);
    private static final Float DEFAULT_SCALE = 1.0F;
    private Rotations rotation = DEFAULT_ROTATION;
    private Float scale = DEFAULT_SCALE;

    public ItemDisplayEntity(EntityType<?> type, World level) {
        super(type, level);
    }

    @Override
    public void baseTick() {
        Rotations rotations1 = entityData.get(ROTATION);

        if (!rotation.equals(rotations1)) {
            setRotation(rotations1);
        }

        float scale = entityData.get(SCALE);

        if (!this.scale.equals(scale)) {
            setScale(scale);
        }

        dimensions = EntitySize.scalable(scale, scale);

        //setEntityBoundingBox(new AxisAlignedBB(posX - 0.5, posY - 0.5, posZ - 0.5D, posX + 0.5, posY + 0.5, posZ + 0.5));
    }

    @Override
    public void refreshDimensions() {
        EntitySize oldSize = this.dimensions;
        EntitySize newSize = this.getDimensions(this.getPose());
        EntityEvent.Size sizeEvent = ForgeEventFactory.getEntitySizeForge(this, this.getPose(), oldSize, newSize, this.getEyeHeight(this.getPose(), newSize));
        newSize = sizeEvent.getNewSize();
        this.dimensions = newSize;
        float w = newSize.width / 2.0f;
        float h = newSize.height / 2.0f;
        setBoundingBox(new AxisAlignedBB(getX() - w, getY() - h, getZ() - w, getX() + w, getY() + h, getZ() + w));

    }

    @Override
    public void setPos(double x, double y, double z) {
        super.setPos(x, y, z);
        float w = dimensions.width / 2.0f;
        float h = dimensions.height / 2.0f;
        setBoundingBox(new AxisAlignedBB(getX() - w, getY() - h, getZ() - w, getX() + w, getY() + h, getZ() + w));
    }

    public Rotations getRotation() {
        return rotation;
    }

    public void setRotation(Rotations vec) {
        rotation = vec;
        entityData.set(ROTATION, vec);
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
        entityData.set(SCALE, scale);
    }

    @Override
    protected void defineSynchedData() {
        entityData.define(ITEM, ItemStack.EMPTY);
        entityData.define(ROTATION, DEFAULT_ROTATION);
        entityData.define(SCALE, DEFAULT_SCALE);
    }

    public ItemStack getItemStack() {
        return entityData.get(ITEM);
    }

    public void setItemStack(ItemStack stack) {
        entityData.set(ITEM, stack);
    }

    @Override
    protected void readAdditionalSaveData(CompoundNBT tag) {
        // /summon mcf:item_display 0.5 130 5.5 {item:{id:"minecraft:redstone",Count:1b},scale:4.0f}
        // /summon mcf:item_display ~5 ~1 ~ {item:{id:"thermalexpansion:frame",Count:1b},scale:4.0f,rotation:[35f,0f,45f]}
        // /summon mcf:item_display ~ ~ ~ {item:{id:"minecraft:redstone",Count:1b}}
        // /summon mcf:item_display 2608 65.5 27 {item:{id:"cathedral:cathedral_gargoyle_demon_stone",Count:1b},scale:4.0f,rotation:[0f,0f,0f]}
        // /kill @e[type=mcf:item_display]

        CompoundNBT itemTag = tag.getCompound("item");
        setItemStack(ItemStack.of(itemTag));

        ListNBT rotations = tag.getList("rotation", 5);
        setRotation(rotations.isEmpty() ? DEFAULT_ROTATION : new Rotations(rotations));

        float scale = tag.getFloat("scale");
        setScale(scale == 0.0f ? DEFAULT_SCALE : scale);
    }

    @Override
    protected void addAdditionalSaveData(CompoundNBT tag) {
        CompoundNBT itemTag = new CompoundNBT();
        ItemStack stack = getItemStack();

        if (!stack.isEmpty()) {
            stack.save(itemTag);
        }

        tag.put("item", itemTag);

        if (!DEFAULT_ROTATION.equals(rotation)) {
            tag.put("rotation", rotation.save());
        }

        if (!DEFAULT_SCALE.equals(scale)) {
            tag.putFloat("scale", scale);
        }
    }

    @Override
    public IPacket<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

}
