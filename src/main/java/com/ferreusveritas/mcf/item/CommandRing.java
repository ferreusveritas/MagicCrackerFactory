package com.ferreusveritas.mcf.item;

import com.ferreusveritas.mcf.peripheral.RemoteReceiverPeripheral;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.common.util.LazyOptional;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.CuriosCapability;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class CommandRing extends CommandItem {

    public CommandRing(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
        return new CapabilityProvider(LazyOptional.of(() -> new ICurio() {
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
        }));
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

    private static final class CapabilityProvider implements ICapabilityProvider {

        final LazyOptional<ICurio> curio;

        CapabilityProvider(LazyOptional<ICurio> curio) {
            this.curio = curio;
        }

        @Nonnull
        @Override
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
            return CuriosCapability.ITEM.orEmpty(cap, this.curio);
        }
    }

}
