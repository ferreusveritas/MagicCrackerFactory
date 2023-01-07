package com.ferreusveritas.mcf.item;

import com.ferreusveritas.mcf.peripheral.RemoteReceiverPeripheral;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.Constants.NBT;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio;

public abstract class CommandRing extends CommandItem implements ICurio {

    public CommandRing(Properties properties) {
        super(properties);
    }

    @Override
    public void onEquip(SlotContext slotContext, ItemStack prevStack) {
        LivingEntity wearerEntity = slotContext.getWearer();
        if (!wearerEntity.level.isClientSide && wearerEntity instanceof PlayerEntity) {
            PlayerEntity wearer = (PlayerEntity) wearerEntity;
            satisfyWorn(wearer);
            updateRing(prevStack, wearer);
        }
    }

    @Override
    public void curioTick(String identifier, int index, LivingEntity livingEntity) {
        if (!livingEntity.level.isClientSide && livingEntity instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) livingEntity;
            if (shouldRun(player)) {
                updateAllRings(player);
                satisfyWorn(player);
            }
        }
    }

    public abstract boolean shouldRun(PlayerEntity player);

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

    public void updateAllRings(PlayerEntity player) {
        CuriosApi.getCuriosHelper().findCurios(player, this).forEach(slot -> updateRing(slot.getStack(), player));
    }

}
