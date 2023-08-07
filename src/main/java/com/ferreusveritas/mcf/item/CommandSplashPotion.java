package com.ferreusveritas.mcf.item;

import com.ferreusveritas.mcf.Registry;
import com.ferreusveritas.mcf.entity.CommandPotionEntity;
import com.ferreusveritas.mcf.peripheral.RemoteReceiverPeripheral;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class CommandSplashPotion extends CommandItem {

    public CommandSplashPotion(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.SPLASH_POTION_THROW, SoundSource.PLAYERS, 0.5F, 0.4F / (world.random.nextFloat() * 0.4F + 0.8F));
        ItemStack item = player.getItemInHand(hand);

        if (!world.isClientSide) {
            CommandPotionEntity potionEntity = Registry.COMMAND_POTION_ENTITY.get().create(world);
            potionEntity.setup(player, item);
            potionEntity.shootFromRotation(player, player.getXRot(), player.getYRot(), -20.0F, 0.5F, 1.0F);
            world.addFreshEntity(potionEntity);
        }

        player.awardStat(Stats.ITEM_USED.get(this));
        if (!player.getAbilities().instabuild) {
            item.shrink(1);
        }

        return InteractionResultHolder.sidedSuccess(item, world.isClientSide);
    }

    public void onImpact(ItemStack stack, Player player, BlockPos blockPos, Direction facing) {
        RemoteReceiverPeripheral.broadcastSplashEvents(player, blockPos, facing, getCommand(stack));
    }

}
