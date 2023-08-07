package com.ferreusveritas.mcf.block;

import com.ferreusveritas.mcf.Registry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public enum PeripheralType {

    CARTOGRAPHER(Registry.CARTOGRAPHER_TILE_ENTITY::get, "peripheral.mcf.cartographer.tooltip"),
    SENTINEL(Registry.SENTINEL_TILE_ENTITY::get, "peripheral.mcf.sentinel.tooltip"),
    TERRAFORMER(Registry.TERRAFORMER_TILE_ENTITY::get, "peripheral.mcf.terraformer.tooltip"),
    REMOTE_RECEIVER(Registry.REMOTE_RECEIVER_TILE_ENTITY::get, "peripheral.mcf.remote_receiver.tooltip"),
    WEB_MODEM(Registry.WEB_MODEM_TILE_ENTITY::get, "peripheral.mcf.web_modem.tooltip");

    private final Supplier<BlockEntityType<?>> blockEntityType;
    private final String descriptionId;

    PeripheralType(Supplier<BlockEntityType<?>> blockEntityType, String descriptionId) {
        this.blockEntityType = blockEntityType;
        this.descriptionId = descriptionId;
    }

    public BlockEntityType<?> getBlockEntityType() {
        return blockEntityType.get();
    }

    public String getName() {
        return this.name().toLowerCase();
    }

    public String getDescriptionId() {
        return descriptionId;
    }

    @Nullable
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return blockEntityType.get().create(pos, state);
    }
}
