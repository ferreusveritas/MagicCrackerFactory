package com.ferreusveritas.mcf.tileentity;

import com.ferreusveritas.mcf.Registry;
import com.ferreusveritas.mcf.peripheral.WebModemPeripheral;
import dan200.computercraft.api.peripheral.IPeripheral;

public class WebModemTileEntity extends MCFPeripheralTileEntity {

    public WebModemTileEntity() {
        super(Registry.WEB_MODEM_TILE_ENTITY.get());
    }

    @Override
    protected IPeripheral createPeripheral() {
        return new WebModemPeripheral(this);
    }

}
