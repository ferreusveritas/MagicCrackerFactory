package com.ferreusveritas.mcf.item;

import com.ferreusveritas.mcf.Registry;
import com.ferreusveritas.mcf.peripheral.RemoteReceiverPeripheral;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PotionEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Stats;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CommandSplashPotion extends CommandItem {

    public CommandSplashPotion(Properties properties) {
        super(properties);
    }

    @Override
    public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stackInHand = player.getItemInHand(hand);
        ItemStack stackThrown = player.isCreative() ? stackInHand.copy() : stackInHand.split(1);
        world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.SPLASH_POTION_THROW, SoundCategory.PLAYERS, 0.5F, 0.4F / (world.random.nextFloat() * 0.4F + 0.8F));

        if (!world.isClientSide) {
            PotionEntity potionEntity = Registry.COMMAND_POTION_ENTITY.get().create(world);
            potionEntity.shootFromRotation(player, player.xRot, player.yRot, -20.0F, 0.5F, 1.0F);
            world.addFreshEntity(potionEntity);
        }

        player.awardStat(Stats.ITEM_USED.get(this));
        return new ActionResult<>(ActionResultType.SUCCESS, stackInHand);
    }

    public void onImpact(ItemStack stack, PlayerEntity player, BlockPos blockPos, Direction facing) {
        RemoteReceiverPeripheral.broadcastSplashEvents(player, blockPos, facing, getCommand(stack));
    }

}
