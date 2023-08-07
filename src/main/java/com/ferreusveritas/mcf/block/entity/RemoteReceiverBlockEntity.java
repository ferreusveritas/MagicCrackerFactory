package com.ferreusveritas.mcf.block.entity;

import com.ferreusveritas.mcf.Registry;
import com.ferreusveritas.mcf.peripheral.RemoteReceiverPeripheral;
import dan200.computercraft.api.peripheral.IPeripheral;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class RemoteReceiverBlockEntity extends MCFPeripheralBlockEntity {

    public RemoteReceiverBlockEntity(BlockPos pos, BlockState state) {
        super(Registry.REMOTE_RECEIVER_TILE_ENTITY.get(), pos, state);
    }

    @Override
    protected IPeripheral createPeripheral() {
        return new RemoteReceiverPeripheral(this);
    }

}
