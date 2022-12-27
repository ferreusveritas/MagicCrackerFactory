package com.ferreusveritas.mcf.item;

import com.ferreusveritas.mcf.peripheral.RemoteReceiverPeripheral;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.UseAction;
import net.minecraft.world.World;

public class CommandPotion extends CommandItem {

    public CommandPotion(Properties properties) {
        super(properties);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, World world, LivingEntity entity) {
        if (entity instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) entity;

            if (!world.isClientSide) {
                RemoteReceiverPeripheral.broadcastPotionEvents(player, getCommand(stack));
            }

            if (!player.isCreative()) {
                stack.shrink(1);
                player.inventory.add(new ItemStack(Items.GLASS_BOTTLE));
            }

        }
        return stack;
    }

    @Override
    public UseAction getUseAnimation(ItemStack pStack) {
        return UseAction.DRINK;
    }

    @Override
    public int getUseDuration(ItemStack pStack) {
        return 32;
    }

}
