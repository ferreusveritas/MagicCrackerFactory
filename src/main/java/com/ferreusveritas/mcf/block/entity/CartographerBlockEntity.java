package com.ferreusveritas.mcf.block.entity;

import com.ferreusveritas.mcf.Registry;
import com.ferreusveritas.mcf.peripheral.CartographerPeripheral;
import dan200.computercraft.api.peripheral.IPeripheral;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;

public class CartographerBlockEntity extends MCFPeripheralBlockEntity {

    // Cache the last accessed map data for efficiency
    private int lastMapNum = -1;
    private MapItemSavedData lastMapData = null;

    public CartographerBlockEntity(BlockPos pos, BlockState state) {
        super(Registry.CARTOGRAPHER_TILE_ENTITY.get(), pos, state);
    }

    @Override
    protected IPeripheral createPeripheral() {
        return new CartographerPeripheral(this);
    }

    public MapItemSavedData getMapData(int mapNum) {
        if (lastMapData != null && lastMapNum == mapNum) {
            return lastMapData;
        } else {
            lastMapNum = mapNum;
            String dataId = "map_" + mapNum;
            lastMapData = level.getMapData(dataId);

            if (lastMapData == null) {
                lastMapData = newMap(level, dataId);
            }

            return lastMapData;
        }
    }

    public MapItemSavedData newMap(Level level, String mapName) {
        MapItemSavedData mapData = MapItemSavedData.createFresh(0, 0, (byte) 0, true, false, level.dimension());
        level.setMapData(mapName, mapData);
        return mapData;
    }

}
