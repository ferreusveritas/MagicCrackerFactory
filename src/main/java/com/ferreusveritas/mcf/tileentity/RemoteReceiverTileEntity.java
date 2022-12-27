package com.ferreusveritas.mcf.tileentity;

import com.ferreusveritas.mcf.Registry;
import com.ferreusveritas.mcf.peripheral.RemoteReceiverPeripheral;
import dan200.computercraft.api.peripheral.IPeripheral;

public class RemoteReceiverTileEntity extends MCFPeripheralTileEntity {

    public RemoteReceiverTileEntity() {
        super(Registry.REMOTE_RECEIVER_TILE_ENTITY.get());
    }

    @Override
    protected IPeripheral createPeripheral() {
        return new RemoteReceiverPeripheral(this);
    }

}
