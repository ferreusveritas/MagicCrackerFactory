package com.ferreusveritas.mcf.block;

import com.ferreusveritas.mcf.Registry;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public enum PeripheralType {

    CARTOGRAPHER(Registry.CARTOGRAPHER_TILE_ENTITY::get, "peripheral.mcf.cartographer.tooltip"),
    SENTINEL(Registry.SENTINEL_TILE_ENTITY::get, "peripheral.mcf.sentinel.tooltip"),
    TERRAFORMER(Registry.TERRAFORMER_TILE_ENTITY::get, "peripheral.mcf.terraformer.tooltip"),
    REMOTE_RECEIVER(Registry.REMOTE_RECEIVER_TILE_ENTITY::get, "peripheral.mcf.remote_receiver.tooltip"),
    WEB_MODEM(Registry.WEB_MODEM_TILE_ENTITY::get, "peripheral.mcf.web_modem.tooltip");

    private final Supplier<TileEntityType<?>> tileEntityType;
    private final String descriptionId;

    PeripheralType(Supplier<TileEntityType<?>> tileEntityType, String descriptionId) {
        this.tileEntityType = tileEntityType;
        this.descriptionId = descriptionId;
    }

    public TileEntityType<?> getTileEntityType() {
        return tileEntityType.get();
    }

    public String getName() {
        return this.name().toLowerCase();
    }

    public String getDescriptionId() {
        return descriptionId;
    }

    @Nullable
    public TileEntity createTileEntity() {
        return tileEntityType.get().create();
    }
}
