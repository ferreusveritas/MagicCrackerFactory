package com.ferreusveritas.mcf.peripheral;

import com.ferreusveritas.mcf.block.entity.CartographerBlockEntity;
import com.google.common.collect.Maps;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundMapItemDataPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.level.saveddata.maps.MapBanner;
import net.minecraft.world.level.saveddata.maps.MapDecoration;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Map;

public class CartographerPeripheral extends MCFPeripheral<CartographerBlockEntity> {

    public CartographerPeripheral(CartographerBlockEntity block) {
        super(block);
    }

    @LuaFunction
    public final int getMapPixel(int mapNum, int x, int z) {
        if (x >= 0 && x < 128 && z >= 0 && z < 128) {
            return block.getMapData(mapNum).colors[x + z * 128];
        }
        return 0;
    }

    @LuaFunction
    public final int setMapPixel(int mapNum, int x, int z, int colorFull) {
        if (x >= 0 && x < 128 && z >= 0 && z < 128) {
            block.getMapData(mapNum).colors[x + z * 128] = (byte) (colorFull >= 0 && colorFull <= (51 * 4) ? colorFull : 0);
        }
        return 0;
    }

    @LuaFunction
    public final Object[] getMapPixels(int mapNum) {
        byte[] colors = block.getMapData(mapNum).colors;
        return new Object[]{Arrays.copyOf(colors, colors.length)};
    }

    @LuaFunction
    public final int setMapPixels(int mapNum, String data) {
        char[] charArray = data.toCharArray();
        MapItemSavedData mapData = block.getMapData(mapNum);
        byte[] byteArray = mapData.colors;

        if (charArray.length == 128 * 128) {
            for (int i = 0; i < charArray.length; i++) {
                char val = charArray[i];
                byteArray[i] = val <= 207 ? (byte) val : 0;
            }
        }

        return 0;
    }

    @LuaFunction
    public final int getMapCenter(int mapNum) {
        return block.getMapData(mapNum).x;
    }

    @LuaFunction
    public final int setMapCenter(int mapNum, int x, int z) {
        MapItemSavedData mapData = block.getMapData(mapNum);
        mapData.x = x;
        mapData.z = z;
        return 0;
    }

    @LuaFunction
    public final int getMapScale(int mapNum) {
        return block.getMapData(mapNum).scale;
    }

    @LuaFunction
    public final int setMapScale(int mapNum, int scale) {
        block.getMapData(mapNum).scale = (byte) scale;
        return 0;
    }

    @LuaFunction
    public final boolean getMapLocked(int mapNum) {
        return block.getMapData(mapNum).locked;
    }

    @LuaFunction
    public final int setMapLocked(int mapNum, boolean locked) {
        block.getMapData(mapNum).locked = locked;
        return 0;
    }

    @LuaFunction
    public final String getMapDimension(int mapNum) {
        return block.getMapData(mapNum).dimension.location().toString();
    }

    @LuaFunction
    public final int setMapDimension(int mapNum, String dimension) throws LuaException {
        ResourceLocation dimensionLocation = ResourceLocation.tryParse(dimension);
        if (dimensionLocation == null) {
            throw new LuaException("Invalid resource location for dimension argument");
        }
        block.getMapData(mapNum).dimension = ResourceKey.create(Registry.DIMENSION_REGISTRY, dimensionLocation);
        return 0;
    }

    @LuaFunction
    public final int copyMap(int srcMapNum, int destMapNum) {
        MapItemSavedData srcMapData = block.getMapData(srcMapNum);
        MapItemSavedData destMapData = block.getMapData(destMapNum);

        if (srcMapData == null || destMapData == null) {
            return -1;
        }

        // Copy all of the map data values
        destMapData.x = srcMapData.x;
        destMapData.z = srcMapData.z;
        destMapData.dimension = srcMapData.dimension;
        destMapData.trackingPosition = srcMapData.trackingPosition;
        destMapData.unlimitedTracking = srcMapData.unlimitedTracking;
        destMapData.scale = srcMapData.scale;
        destMapData.colors = srcMapData.colors.clone();
        destMapData.locked = srcMapData.locked;

        destMapData.bannerMarkers.clear();
        destMapData.bannerMarkers.putAll(srcMapData.bannerMarkers);

        destMapData.decorations.clear();
        destMapData.decorations.putAll(srcMapData.decorations);
        destMapData.trackedDecorationCount = srcMapData.trackedDecorationCount;

        return 0;
    }

    @LuaFunction
    public final int swapMapData(int mapNumA, int mapNumB) {
        MapItemSavedData mapDataA = block.getMapData(mapNumA);
        MapItemSavedData mapDataB = block.getMapData(mapNumB);

        if (mapDataA == null || mapDataB == null) {
            return -1;
        }

        //Swap all of the map data values between A and B
        {
            int temp = mapDataA.x;
            mapDataA.x = mapDataB.x;
            mapDataB.x = temp;
        }
        {
            int temp = mapDataA.z;
            mapDataA.z = mapDataB.z;
            mapDataB.z = temp;
        }
        {
            ResourceKey<Level> temp = mapDataA.dimension;
            mapDataA.dimension = mapDataB.dimension;
            mapDataB.dimension = temp;
        }
        {
            boolean temp = mapDataA.trackingPosition;
            mapDataA.trackingPosition = mapDataB.trackingPosition;
            mapDataB.trackingPosition = temp;
        }
        {
            boolean temp = mapDataA.unlimitedTracking;
            mapDataA.unlimitedTracking = mapDataB.unlimitedTracking;
            mapDataB.unlimitedTracking = temp;
        }
        {
            byte temp = mapDataA.scale;
            mapDataA.scale = mapDataB.scale;
            mapDataB.scale = temp;
        }
        {
            byte[] temp = mapDataA.colors;
            mapDataA.colors = mapDataB.colors;
            mapDataB.colors = temp;
        }
        {
            boolean temp = mapDataA.locked;
            mapDataA.locked = mapDataB.locked;
            mapDataB.locked = temp;
        }
        {
            Map<String, MapBanner> temp = Maps.newLinkedHashMap();
            temp.putAll(mapDataA.bannerMarkers);
            mapDataA.bannerMarkers.clear();
            mapDataA.bannerMarkers.putAll(mapDataB.bannerMarkers);
            mapDataB.bannerMarkers.clear();
            mapDataB.bannerMarkers.putAll(temp);
        }
        {
            Map<String, MapDecoration> temp = Maps.newLinkedHashMap();
            temp.putAll(mapDataA.decorations);
            mapDataA.decorations.clear();
            mapDataA.decorations.putAll(mapDataB.decorations);
            mapDataB.decorations.clear();
            mapDataB.decorations.putAll(temp);
        }
        {
            int temp = mapDataA.trackedDecorationCount;
            mapDataA.trackedDecorationCount = mapDataB.trackedDecorationCount;
            mapDataB.trackedDecorationCount = temp;
        }

        return 0;
    }

    @LuaFunction
    public final int updateMap(int mapNum) {
        MapItemSavedData mapData = block.getMapData(mapNum);
        return updateMap(mapData, mapNum, null);
    }

    @LuaFunction
    public final int updateWholeMap(int mapNum) {
        return updateMapSection(mapNum, 0, 0, 127, 127);
    }
    
    @LuaFunction
    public final int updateMapSection(int mapNum, int startX, int startY, int endX, int endY) {
        MapItemSavedData mapData = block.getMapData(mapNum);
        return updateMap(mapData, mapNum, createPatch(mapData.colors, startX, startY, endX, endY));
    }

    private static MapItemSavedData.MapPatch createPatch(byte[] mapColors, int startX, int startY, int endX, int endY) {
        int width = endX + 1 - startX;
        int height = endY + 1 - startY;
        byte[] colors = new byte[width * height];

        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                colors[x + y * width] = mapColors[startX + x + (startY + y) * 128];
            }
        }

        return new MapItemSavedData.MapPatch(startX, startY, width, height, colors);
    }

    private int updateMap(MapItemSavedData mapData, int mapNum, @Nullable MapItemSavedData.MapPatch patch) {
        mapData.setDirty(); // Mark as dirty so the changes save to disk

        // Create update packet, sending to each player
        Packet<?> packet = new ClientboundMapItemDataPacket(mapNum, mapData.scale, mapData.locked, mapData.decorations.values(), patch);
        for (Player player : block.getLevel().players()) {
            if (player instanceof ServerPlayer serverPlayer) {
                serverPlayer.connection.send(packet);
            }
        }

        return 0;
    }

    @LuaFunction
    public final int getBlockMapColor(int x, int y, int z) {
        BlockPos pos = new BlockPos(x, y, z);
        return block.getLevel().getBlockState(pos).getMapColor(block.getLevel(), pos).col;
    }

    @LuaFunction
    public final Object[] getRGBfromMapColor(int i) {
        int arg = i;
        int color = arg >> 2;
        int index = arg & 3;

        if (color >= 0 && color < 64) {
            MaterialColor mapColor = MaterialColor.byId(color);
            int rgbInt = mapColor.calculateRGBColor(MaterialColor.Brightness.byId(index));
            return obj((rgbInt >> 16) & 0xFF, (rgbInt >> 8) & 0xFF, rgbInt & 0xFF);
        }

        return obj(0, 0, 0);
    }

}
