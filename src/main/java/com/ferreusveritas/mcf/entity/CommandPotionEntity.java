package com.ferreusveritas.mcf.entity;

import com.ferreusveritas.mcf.item.CommandSplashPotion;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.network.NetworkHooks;

public class CommandPotionEntity extends ThrownPotion {

    public CommandPotionEntity(EntityType<? extends CommandPotionEntity> type, Level level) {
        super(type, level);
    }

    public void setup(Player thrower, ItemStack item) {
        this.setOwner(thrower);
        this.setItem(item);
        this.setPos(thrower.getX(), thrower.getEyeY() - 0.1F, thrower.getZ());
    }

    @Override
    protected void onHit(HitResult hit) {
        ItemStack item = this.getItem();

        if (!this.level.isClientSide) {
            Entity thrower = getOwner();
            int color = 0;

            if (thrower instanceof Player && hit.getType() == HitResult.Type.BLOCK && item.getItem() instanceof CommandSplashPotion potionItem) {
                BlockHitResult blockResult = (BlockHitResult) hit;
                potionItem.onImpact(item, (Player) thrower, blockResult.getBlockPos(), blockResult.getDirection());
                color = potionItem.getColor(item, 0);
            }

            this.level.levelEvent(LevelEvent.PARTICLES_SPELL_POTION_SPLASH, this.blockPosition(), color);
            this.discard();
        }
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

}
