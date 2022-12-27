package com.ferreusveritas.mcf.tileentity;

import com.ferreusveritas.mcf.Registry;
import com.ferreusveritas.mcf.peripheral.WebModemPeripheral;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;

import java.util.HashMap;
import java.util.Map;

public class WebModemTileEntity extends MCFPeripheralTileEntity {

    private final Map<Integer, IComputerAccess> m_attachedComputers = new HashMap<>();

    public WebModemTileEntity() {
        super(Registry.WEB_MODEM_TILE_ENTITY.get());
    }

    @Override
    protected IPeripheral createPeripheral() {
        return new WebModemPeripheral(this);
    }

}
