package com.ferreusveritas.mcf.tileentity;

import com.ferreusveritas.mcf.Registry;
import com.ferreusveritas.mcf.peripheral.SentinelPeripheral;
import dan200.computercraft.api.peripheral.IPeripheral;

public class SentinelTileEntity extends MCFPeripheralTileEntity {

    public SentinelTileEntity() {
        super(Registry.SENTINEL_TILE_ENTITY.get());
    }

    @Override
    protected IPeripheral createPeripheral() {
        return new SentinelPeripheral(this);
    }

}
