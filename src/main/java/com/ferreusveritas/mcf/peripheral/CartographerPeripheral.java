package com.ferreusveritas.mcf.peripheral;

import com.ferreusveritas.mcf.tileentity.CartographerTileEntity;
import com.google.common.collect.Maps;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.server.SMapDataPacket;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapData;
import net.minecraft.world.storage.MapDecoration;

import java.util.Arrays;
import java.util.Map;

public class CartographerPeripheral extends MCFPeripheral<CartographerTileEntity> {

    public CartographerPeripheral(CartographerTileEntity block) {
        super(block);
    }

    @LuaFunction
    public int getMapPixel(int mapNum, int x, int z) {
        if (x >= 0 && x < 128 && z >= 0 && z < 128) {
            return block.getMapData(mapNum).colors[x + z * 128];
        }
        return 0;
    }

    @LuaFunction
    public int setMapPixel(int mapNum, int x, int z, int colorFull) {
        if (x >= 0 && x < 128 && z >= 0 && z < 128) {
            block.getMapData(mapNum).colors[x + z * 128] = (byte) (colorFull >= 0 && colorFull <= (51 * 4) ? colorFull : 0);
        }
        return 0;
    }

    @LuaFunction
    public Object[] getMapPixels(int mapNum) {
        byte[] colors = block.getMapData(mapNum).colors;
        return new Object[]{Arrays.copyOf(colors, colors.length)};
    }

    @LuaFunction
    public int setMapPixels(int mapNum, String data) {
        char[] charArray = data.toCharArray();
        MapData mapData = block.getMapData(mapNum);
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
    public int getMapCenter(int mapNum) {
        return block.getMapData(mapNum).x;
    }

    @LuaFunction
    public int setMapCenter(int mapNum, int x, int z) {
        MapData mapData = block.getMapData(mapNum);
        mapData.x = x;
        mapData.z = z;
        return 0;
    }

    @LuaFunction
    public int getMapScale(int mapNum) {
        return block.getMapData(mapNum).scale;
    }

    @LuaFunction
    public int setMapScale(int mapNum, byte scale) {
        block.getMapData(mapNum).scale = scale;
        return 0;
    }

    @LuaFunction
    public boolean getMapLocked(int mapNum) {
        return block.getMapData(mapNum).locked;
    }

    @LuaFunction
    public int setMapLocked(int mapNum, boolean locked) {
        block.getMapData(mapNum).locked = locked;
        return 0;
    }

    @LuaFunction
    public String getMapDimension(int mapNum) {
        return block.getMapData(mapNum).dimension.location().toString();
    }

    @LuaFunction
    public int setMapDimension(int mapNum, String dimension) throws LuaException {
        ResourceLocation dimensionLocation = ResourceLocation.tryParse(dimension);
        if (dimensionLocation == null) {
            throw new LuaException("Invalid resource location for dimension argument");
        }
        block.getMapData(mapNum).dimension = RegistryKey.create(Registry.DIMENSION_REGISTRY, dimensionLocation);
        return 0;
    }

    @LuaFunction
    public int copyMap(int srcMapNum, int destMapNum) {
        MapData srcMapData = block.getMapData(srcMapNum);
        MapData destMapData = block.getMapData(destMapNum);

        if (srcMapData == null || destMapData == null) {
            return -1;
        }

        // Copy all of the map data values
        destMapData.x = srcMapData.x;
        destMapData.z = srcMapData.x;
        destMapData.dimension = srcMapData.dimension;
        destMapData.trackingPosition = srcMapData.trackingPosition;
        destMapData.unlimitedTracking = srcMapData.unlimitedTracking;
        destMapData.scale = srcMapData.scale;
        destMapData.colors = srcMapData.colors.clone();
        destMapData.locked = srcMapData.locked;

        destMapData.decorations.clear();
        destMapData.decorations.putAll(srcMapData.decorations);

        destMapData.setDirty();
        return 0;
    }

    @LuaFunction
    public int swapMapData(int mapNumA, int mapNumB) {
        MapData mapDataA = block.getMapData(mapNumA);
        MapData mapDataB = block.getMapData(mapNumB);

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
            RegistryKey<World> temp = mapDataA.dimension;
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
            Map<String, MapDecoration> temp = Maps.newLinkedHashMap();
            temp.putAll(mapDataA.decorations);
            mapDataA.decorations.clear();
            mapDataA.decorations.putAll(mapDataB.decorations);
            mapDataB.decorations.clear();
            mapDataB.decorations.putAll(temp);
        }

        mapDataA.setDirty();
        mapDataB.setDirty();

        return 0;
    }

    @LuaFunction
    public int updateMap(int mapNum) {
        MapData mapData = block.getMapData(mapNum);
        mapData.setDirty(); // Mark as dirty so the changes save to disk
        IPacket<?> packet = new SMapDataPacket(mapNum, mapData.scale, mapData.trackingPosition, mapData.locked, mapData.decorations.values(), mapData.colors, 0, 0, 128, 128);

        for (PlayerEntity player : block.getLevel().players()) {
            if (player instanceof ServerPlayerEntity) {
                ((ServerPlayerEntity) player).connection.send(packet);
            }
        }

        return 0;
    }

    @LuaFunction
    public int getBlockMapColor(int x, int y, int z) {
        BlockPos pos = new BlockPos(x, y, z);
        return block.getLevel().getBlockState(pos).getMapColor(block.getLevel(), pos).col;
    }

    @LuaFunction
    public Object[] getRGBfromMapColor(int i) {
        int arg = i;
        int color = arg >> 2;
        int index = arg & 3;

        if (color >= 0 && color < 64) {
            MaterialColor mapColor = MaterialColor.MATERIAL_COLORS[color];
            if (mapColor != null) {
                int rgbInt = mapColor.calculateRGBColor(index);
                return obj((rgbInt >> 16) & 0xFF, (rgbInt >> 8) & 0xFF, rgbInt & 0xFF);
            }
        }

        return obj(0, 0, 0);
    }

}
