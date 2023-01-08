package com.ferreusveritas.mcf.entity;

import com.ferreusveritas.mcf.item.CommandSplashPotion;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PotionEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.IPacket;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.network.NetworkHooks;

public class CommandPotionEntity extends PotionEntity {

    public CommandPotionEntity(EntityType<? extends CommandPotionEntity> type, World world) {
        super(type, world);
    }

    public void setup(PlayerEntity thrower, ItemStack item) {
        this.setOwner(thrower);
        this.setItem(item);
        this.setPos(thrower.getX(), thrower.getEyeY() - 0.1F, thrower.getZ());
    }

    @Override
    protected void onHit(RayTraceResult result) {
        ItemStack item = this.getItem();

        if (!this.level.isClientSide) {
            Entity thrower = getOwner();
            int color = 0;

            if (thrower instanceof PlayerEntity && result.getType() == RayTraceResult.Type.BLOCK && item.getItem() instanceof CommandSplashPotion) {
                CommandSplashPotion potionItem = (CommandSplashPotion) item.getItem();
                BlockRayTraceResult blockResult = (BlockRayTraceResult) result;
                potionItem.onImpact(item, (PlayerEntity) thrower, blockResult.getBlockPos(), blockResult.getDirection());
                color = potionItem.getColor(item, 0);
            }

            this.level.levelEvent(Constants.WorldEvents.POTION_IMPACT, this.blockPosition(), color);
            this.remove();
        }
    }

    @Override
    public IPacket<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

}
