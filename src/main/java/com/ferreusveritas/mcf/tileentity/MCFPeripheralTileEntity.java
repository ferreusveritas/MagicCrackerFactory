package com.ferreusveritas.mcf.tileentity;

import com.ferreusveritas.mcf.util.Capabilities;
import dan200.computercraft.api.peripheral.IPeripheral;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public abstract class MCFPeripheralTileEntity extends TileEntity {

    private LazyOptional<IPeripheral> peripheralCap;

    public MCFPeripheralTileEntity(TileEntityType<?> type) {
        super(type);
    }

    @Override
    protected void invalidateCaps() {
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

    public void appendHoverText(ItemStack stack, @Nullable IBlockReader level, List<ITextComponent> tooltip, ITooltipFlag flag) {
        tooltip.add(new StringTextComponent("ComputerCraft Peripheral").withStyle(TextFormatting.GOLD));
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
