package com.ferreusveritas.mcf.block.entity;

import com.ferreusveritas.mcf.util.Capabilities;
import dan200.computercraft.api.peripheral.IPeripheral;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public abstract class MCFPeripheralBlockEntity extends BlockEntity {

    private LazyOptional<IPeripheral> peripheralCap;

    public MCFPeripheralBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        if (peripheralCap != null) {
            peripheralCap.invalidate();
        }
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == Capabilities.PERIPHERAL) {
            if (peripheralCap == null) {
                peripheralCap = LazyOptional.of(this::createPeripheral);
            }
            return peripheralCap.cast();
        }
        return super.getCapability(cap, side);
    }

    protected abstract IPeripheral createPeripheral();

    public void appendHoverText(ItemStack stack, BlockGetter level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(new TextComponent("ComputerCraft Peripheral").withStyle(ChatFormatting.GOLD));
    }

    @Override
    public boolean equals(Object other) {
        return this == other;
    }

    @Override
    public int hashCode() {
        return worldPosition.hashCode() ^ level.dimension().hashCode();
    }

}
