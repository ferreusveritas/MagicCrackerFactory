package com.ferreusveritas.mcf.peripheral;

import com.ferreusveritas.mcf.tileentity.MCFPeripheralTileEntity;
import dan200.computercraft.api.peripheral.IPeripheral;

public abstract class MCFPeripheral<T extends MCFPeripheralTileEntity> implements IPeripheral {

    protected final T block;

    public MCFPeripheral(T block) {
        this.block = block;
    }

    protected static Object[] obj(Object... args) {
        return args;
    }

    @Override
    public String getType() {
        return block.getType().getRegistryName().getPath();
    }

    @Override
    public boolean equals(IPeripheral other) {
        return this == other;
    }

}
