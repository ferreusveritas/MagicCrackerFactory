package com.ferreusveritas.mcf.block.entity;

import com.ferreusveritas.mcf.Registry;
import com.ferreusveritas.mcf.peripheral.WebModemPeripheral;
import dan200.computercraft.api.peripheral.IPeripheral;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class WebModemBlockEntity extends MCFPeripheralBlockEntity {

    public WebModemBlockEntity(BlockPos pos, BlockState state) {
        super(Registry.WEB_MODEM_TILE_ENTITY.get(), pos, state);
    }

    @Override
    protected IPeripheral createPeripheral() {
        return new WebModemPeripheral(this);
    }

}
