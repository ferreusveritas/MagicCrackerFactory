package com.ferreusveritas.mcf.item;

import com.ferreusveritas.mcf.peripheral.RemoteReceiverPeripheral;
import lazy.baubles.api.BaublesAPI;
import lazy.baubles.api.bauble.BaubleType;
import lazy.baubles.api.bauble.IBauble;
import lazy.baubles.api.cap.IBaublesItemHandler;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.Constants.NBT;

import java.util.function.Predicate;

public abstract class CommandRing extends CommandItem implements IBauble {

    public CommandRing(Properties properties) {
        super(properties);
    }

    @Override
    public BaubleType getBaubleType(ItemStack arg0) {
        return BaubleType.RING;
    }

    @Override
    public void onEquipped(LivingEntity entityLiving, ItemStack itemstack) {
        IBauble.super.onEquipped(entityLiving, itemstack);
        if (!entityLiving.level.isClientSide && entityLiving instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) entityLiving;
            satisfyWorn(player);
            updateRing(itemstack, player);
        }
    }

    @Override
    public void onWornTick(LivingEntity entityLiving, ItemStack itemstack) {
        if (!entityLiving.level.isClientSide && entityLiving instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) entityLiving;
            if (shouldRun(player)) {
                processWorn(player);
                satisfyWorn(player);
            }
        }
    }

    public abstract boolean shouldRun(PlayerEntity player);

    public abstract void processWorn(PlayerEntity player);

    public abstract void satisfyWorn(PlayerEntity player);

    public void updateRing(ItemStack ring, PlayerEntity player) {
        if (ring.hasTag()) {
            CompoundNBT tag = ring.getTag();
            if (tag.contains("command", NBT.TAG_STRING)) {
                String command = tag.getString("command");
                if (!command.isEmpty()) {
                    RemoteReceiverPeripheral.broadcastRingEvents(player, command);
                }
            }
        }
    }

    public void updateAllRings(PlayerEntity player, Predicate<ItemStack> predicate) {
        @SuppressWarnings("deprecation")
        IBaublesItemHandler inventory = BaublesAPI.getBaublesHandler(player).orElse(null);
        int slots = inventory.getSlots();

        for (int i = 0; i < slots; i++) {
            ItemStack baubleItemStack = inventory.getStackInSlot(i);
            if (predicate.test(baubleItemStack)) {
                updateRing(baubleItemStack, player);
            }
        }
    }

}
