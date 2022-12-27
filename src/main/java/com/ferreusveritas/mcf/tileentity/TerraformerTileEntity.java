package com.ferreusveritas.mcf.tileentity;

import com.ferreusveritas.mcf.Registry;
import com.ferreusveritas.mcf.peripheral.TerraformerPeripheral;
import dan200.computercraft.api.peripheral.IPeripheral;

public class TerraformerTileEntity extends MCFPeripheralTileEntity {

    public TerraformerTileEntity() {
        super(Registry.TERRAFORMER_TILE_ENTITY.get());
    }

    @Override
    protected IPeripheral createPeripheral() {
        return new TerraformerPeripheral(this);
    }

}