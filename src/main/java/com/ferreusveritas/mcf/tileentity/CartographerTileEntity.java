package com.ferreusveritas.mcf.tileentity;

import com.ferreusveritas.mcf.Registry;
import com.ferreusveritas.mcf.peripheral.CartographerPeripheral;
import dan200.computercraft.api.peripheral.IPeripheral;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapData;

public class CartographerTileEntity extends MCFPeripheralTileEntity {

    // Cache the last accessed map data for efficiency
    private int lastMapNum = -1;
    private MapData lastMapData = null;

    public CartographerTileEntity() {
        super(Registry.CARTOGRAPHER_TILE_ENTITY.get());
    }

    @Override
    protected IPeripheral createPeripheral() {
        return new CartographerPeripheral(this);
    }

    public MapData getMapData(int mapNum) {
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

    public MapData newMap(World world, String dataId) {
        MapData mapData = new MapData(dataId);
        world.setMapData(mapData);
        mapData.scale = (byte) 0;
        mapData.setOrigin(mapData.scale, 0, 0);
        mapData.dimension = world.dimension();
        mapData.trackingPosition = true;
        mapData.unlimitedTracking = false;
        mapData.setDirty();
        return mapData;
    }

}
