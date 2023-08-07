package com.ferreusveritas.mcf.block.entity;

import com.ferreusveritas.mcf.Registry;
import com.ferreusveritas.mcf.peripheral.SentinelPeripheral;
import dan200.computercraft.api.peripheral.IPeripheral;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class SentinelBlockEntity extends MCFPeripheralBlockEntity {

    public SentinelBlockEntity(BlockPos pos, BlockState state) {
        super(Registry.SENTINEL_TILE_ENTITY.get(), pos, state);
    }

    @Override
    protected IPeripheral createPeripheral() {
        return new SentinelPeripheral(this);
    }

}
