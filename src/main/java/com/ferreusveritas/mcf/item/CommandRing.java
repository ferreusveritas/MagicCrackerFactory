package com.ferreusveritas.mcf.item;

import com.ferreusveritas.mcf.peripheral.RemoteReceiverPeripheral;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
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
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag tag) {
        return new CapabilityProvider(LazyOptional.of(() -> new ICurio() {
            @Override
            public ItemStack getStack() {
                return stack;
            }

            @Override
            public void onEquip(SlotContext slotContext, ItemStack prevStack) {
                LivingEntity wearerEntity = slotContext.getWearer();
                if (!wearerEntity.level.isClientSide && wearerEntity instanceof Player wearer) {
                    satisfyWorn(wearer);
                    updateRing(prevStack, wearer);
                }

            }

            @Override
            public void curioTick(String identifier, int index, LivingEntity livingEntity) {
                if (!livingEntity.level.isClientSide && livingEntity instanceof Player) {
                    Player player = (Player) livingEntity;
                    if (shouldRun(player)) {
                        updateAllRings(player);
                        satisfyWorn(player);
                    }
                }
            }
        }));
    }

    public abstract boolean shouldRun(Player player);

    public abstract void satisfyWorn(Player player);

    public void updateRing(ItemStack ring, Player player) {
        if (ring.hasTag()) {
            CompoundTag tag = ring.getTag();
            if (tag.contains("command", Tag.TAG_STRING)) {
                String command = tag.getString("command");
                if (!command.isEmpty()) {
                    RemoteReceiverPeripheral.broadcastRingEvents(player, command);
                }
            }
        }
    }

    public void updateAllRings(Player player) {
        CuriosApi.getCuriosHelper().findCurios(player, this).forEach(slot -> updateRing(slot.stack(), player));
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
