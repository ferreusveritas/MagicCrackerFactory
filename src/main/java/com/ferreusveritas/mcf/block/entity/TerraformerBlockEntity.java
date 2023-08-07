package com.ferreusveritas.mcf.block.entity;

import com.ferreusveritas.mcf.Registry;
import com.ferreusveritas.mcf.peripheral.TerraformerPeripheral;
import dan200.computercraft.api.peripheral.IPeripheral;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class TerraformerBlockEntity extends MCFPeripheralBlockEntity {

    public TerraformerBlockEntity(BlockPos pos, BlockState state) {
        super(Registry.TERRAFORMER_TILE_ENTITY.get(), pos, state);
    }

    @Override
    protected IPeripheral createPeripheral() {
        return new TerraformerPeripheral(this);
    }

}