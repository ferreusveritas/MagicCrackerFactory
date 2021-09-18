package com.ferreusveritas.mcf.entities;

import com.ferreusveritas.mcf.items.CommandSplashPotion;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class EntityCommandPotion extends EntityPotion {

	private static final DataParameter<ItemStack> ITEM = EntityDataManager.<ItemStack>createKey(EntityPotion.class, DataSerializers.ITEM_STACK);

	public EntityCommandPotion(World worldIn) {
		super(worldIn);
	}

	public EntityCommandPotion(World world, EntityPlayer playerIn, ItemStack itemstack1) {
		super(world, playerIn, itemstack1);
	}

	public EntityCommandPotion(World worldIn, double x, double y, double z, ItemStack potionDamageIn) {
		super(worldIn, x, y, z, potionDamageIn);
	}
	
	protected void onImpact(RayTraceResult result) {
		ItemStack itemstack = this.getPotion();
		
		if (!this.world.isRemote) {
			EntityLivingBase thrower = getThrower();
			int color = 0;
			
			if(thrower instanceof EntityPlayer) {
				if (result.typeOfHit == RayTraceResult.Type.BLOCK) {
					if(itemstack.getItem() instanceof CommandSplashPotion) {
						CommandSplashPotion potionItem = (CommandSplashPotion) itemstack.getItem();
						potionItem.OnImpact(itemstack, (EntityPlayer) thrower, result.getBlockPos(), result.sideHit);
						color = potionItem.getColor(itemstack, 0);
					}
				}
			}
			
			boolean hasInstantEffect = true;
			int i = hasInstantEffect ? 2007 : 2002;
			this.world.playEvent(i, new BlockPos(this), color);
			this.setDead();
		}
		
	}
	
	@Override
	protected void entityInit() {
		this.getDataManager().register(ITEM, ItemStack.EMPTY);
	}
	
	@Override
	public ItemStack getPotion() {
		return (ItemStack)this.getDataManager().get(ITEM);
	}
	
	@Override
	public void setItem(ItemStack stack) {
		this.getDataManager().set(ITEM, stack);
		this.getDataManager().setDirty(ITEM);
	}
	
}
