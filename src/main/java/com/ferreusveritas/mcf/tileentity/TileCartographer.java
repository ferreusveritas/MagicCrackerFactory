package com.ferreusveritas.mcf.tileentity;

import java.util.Map;
import java.util.Map.Entry;

import com.ferreusveritas.mcf.util.CommandManager;
import com.ferreusveritas.mcf.util.MethodDescriptor;
import com.ferreusveritas.mcf.util.MethodDescriptor.SyncProcess;

import net.minecraft.block.material.MapColor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.SPacketMaps;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.storage.MapData;
import net.minecraft.world.storage.MapDecoration;

public class TileCartographer extends MCFPeripheral  {
	
	public TileCartographer() {
		super("cartographer");
	}

	public enum ComputerMethod {
		getMapNum("n", "mapNum", (world, peri, args) -> obj(getTool(peri).getMapNum()) ),
		setMapNum("n", "mapNum", (world, peri, args) -> obj(getTool(peri).setMapNum(args.i())) ),
		getMapPixel("nn", "x, z", (world, peri, args) -> obj(getTool(peri).getMapPixel(args.i(0), args.i(1))) ),
		setMapPixel("nnn", "x, z, colorIndex", (world, peri, args) -> obj(getTool(peri).setMapPixel(args.i(0), args.i(1), args.i(2))) ),
		getMapCenter("", "", (world, peri, args) -> obj(getTool(peri).getCurrMapData().xCenter, getTool(peri).getCurrMapData().zCenter ) ),
		setMapCenter("nn", "x, z", (world, peri, args) -> obj(getTool(peri).setMapCenter(args.i(0), args.i(1))) ),
		getMapScale("", "", (world, peri, args) -> obj(getTool(peri).getCurrMapData().scale) ),
		setMapScale("n", "scale", (world, peri, args) -> obj(getTool(peri).getCurrMapData().scale = (byte) MathHelper.clamp(args.i(), 0, 4)) ),
		getMapDimension("", "",	(world, peri, args) -> obj(getTool(peri).getCurrMapData().dimension) ),
		setMapDimension("n", "dimension", (world, peri, args) -> obj(getTool(peri).getCurrMapData().dimension = args.i()) ),
		copyMapData("nn", "mapA, mapB", (world, peri, args) -> obj(getTool(peri).copyMapData(args.i(0), args.i(1))) ),
		swapMapData("nn", "mapA, mapB", (world, peri, args) -> obj(getTool(peri).swapMapData(args.i(0), args.i(1))) ),
		updateMap("", "", (world, peri, args) -> obj(getTool(peri).updateMap()) ),
		getBlockMapColor("nnn", "xCoord, yCoord, zCoord", (world, peri, args) -> obj(getTool(peri).getBlockMapColor(args.p())) ),
		getRGBfromMapColor("n", "index", (world, peri, args) -> getRGBfromMapColor(args.i()) );
		
		final MethodDescriptor md;
		private ComputerMethod(String argTypes, String args, SyncProcess process) { md = new MethodDescriptor(argTypes, args, process); }
		
		public static TileCartographer getTool(MCFPeripheral peripheral) {
			return (TileCartographer) peripheral;
		}
		
	}
	
	static CommandManager<ComputerMethod> commandManager = new CommandManager<>(ComputerMethod.class);
	
	@Override
	public CommandManager getCommandManager() {
		return commandManager;
	}
	
	int mapNum = 0;
	MapData currMapData;
	
	public MapData getCurrMapData() {
		return currMapData != null ? currMapData : setCurrMapData(0);
	}
	
	public MapData setCurrMapData(int mapNum) {
		this.mapNum = mapNum;
		this.currMapData = (MapData) world.loadData(MapData.class, "map_" + mapNum);
		return this.currMapData;
	}
	
	public int getMapNum() {
		return mapNum;
	}
	
	public int setMapNum(int mapNum) {
		setCurrMapData(mapNum);
		return this.mapNum;
	}
	
	public int getMapPixel(int x, int z) {
		if(x >= 0 && x < 128 && z >= 0 && z < 128) {
			return getCurrMapData().colors[x + z * 128];
		}
		return 0;
	}
	
	public int setMapPixel(int x, int z, int colorFull) {
		if(x >= 0 && x < 128 && z >= 0 && z < 128) {
			getCurrMapData().colors[x + z * 128] = (byte) (colorFull >= 0 && colorFull <= (51 * 4) ? colorFull : 0);
		}
		return mapNum;
	}

	public void setMapPixel(int x, int z, int color52, int index4) {
		setMapPixel(x, z, color52 * 4 | index4);
	}
	
	public int setMapCenter(int x, int z) {
		getCurrMapData().xCenter = x;
		getCurrMapData().zCenter = z;
		return mapNum;
	}

	public int copyMapData(int mapSrc, int mapDst) {

		MapData mapDataSrc = (MapData) world.loadData(MapData.class, "map_" + mapSrc);
		MapData mapDataDst = (MapData) world.loadData(MapData.class, "map_" + mapDst);
		
		if(mapDataSrc == null || mapDataDst == null) {
			return -1;
		}
		
		//Copy all of the map data values
		mapDataDst.xCenter = mapDataSrc.xCenter;
		mapDataDst.zCenter = mapDataSrc.zCenter;
		mapDataDst.dimension = mapDataSrc.dimension;
		mapDataDst.trackingPosition = mapDataSrc.trackingPosition;
		mapDataDst.unlimitedTracking = mapDataSrc.unlimitedTracking;
		mapDataDst.scale = mapDataSrc.scale;
		mapDataDst.colors = mapDataSrc.colors.clone();

		mapDataDst.mapDecorations.clear();
		for(Entry<String, MapDecoration> entry : mapDataSrc.mapDecorations.entrySet()) {
			mapDataDst.mapDecorations.put(entry.getKey(), entry.getValue());	
		}

		mapDataDst.markDirty();
		Packet<?> packetDst = new SPacketMaps(mapDst, mapDataDst.scale, mapDataDst.trackingPosition, mapDataDst.mapDecorations.values(), mapDataDst.colors, 0, 0, 128, 128);
		
		for(EntityPlayer player : world.playerEntities) {
			if(player instanceof EntityPlayerMP) {
				((EntityPlayerMP)player).connection.sendPacket(packetDst);
			}
		}
		
		return 0;
	}

	
	public int swapMapData(int mapNumA, int mapNumB) {

		MapData mapDataA = (MapData) world.loadData(MapData.class, "map_" + mapNumA);
		MapData mapDataB = (MapData) world.loadData(MapData.class, "map_" + mapNumB);
		
		if(mapDataA == null || mapDataB == null) {
			return -1;
		}
		
		//Swap all of the map data values between A and B
		{ int temp = mapDataA.xCenter; mapDataA.xCenter = mapDataB.xCenter; mapDataB.xCenter = temp; }
		{ int temp = mapDataA.zCenter; mapDataA.zCenter = mapDataB.zCenter; mapDataB.zCenter = temp; }
		{ int temp = mapDataA.dimension; mapDataA.dimension = mapDataB.dimension; mapDataB.dimension = temp; }
		{ boolean temp = mapDataA.trackingPosition; mapDataA.trackingPosition = mapDataB.trackingPosition; mapDataB.trackingPosition = temp; }
		{ boolean temp = mapDataA.unlimitedTracking; mapDataA.unlimitedTracking = mapDataB.unlimitedTracking; mapDataB.unlimitedTracking = temp; }
		{ byte temp = mapDataA.scale; mapDataA.scale = mapDataB.scale; mapDataB.scale = temp; }
		{ byte[] temp = mapDataA.colors; mapDataA.colors = mapDataB.colors; mapDataB.colors = temp; }
		{ Map<String, MapDecoration> temp = mapDataA.mapDecorations; mapDataA.mapDecorations = mapDataB.mapDecorations; mapDataB.mapDecorations = temp; }

		mapDataA.markDirty();
		mapDataB.markDirty();
		
		Packet<?> packetA = new SPacketMaps(mapNumA, mapDataA.scale, mapDataA.trackingPosition, mapDataA.mapDecorations.values(), mapDataA.colors, 0, 0, 128, 128);
		Packet<?> packetB = new SPacketMaps(mapNumB, mapDataB.scale, mapDataB.trackingPosition, mapDataB.mapDecorations.values(), mapDataB.colors, 0, 0, 128, 128);
		
		for(EntityPlayer player : world.playerEntities) {
			if(player instanceof EntityPlayerMP) {
				((EntityPlayerMP)player).connection.sendPacket(packetA);
				((EntityPlayerMP)player).connection.sendPacket(packetB);
			}
		}
		
		return 0;
	}
	
	public int updateMap() {
		MapData mapData = getCurrMapData();
		mapData.markDirty();//Mark as dirty so the changes save to disk
		Packet<?> packet = new SPacketMaps(mapNum, mapData.scale, mapData.trackingPosition, mapData.mapDecorations.values(), mapData.colors, 0, 0, 128, 128);

		for(EntityPlayer player : world.playerEntities) {
			if(player instanceof EntityPlayerMP) {
				((EntityPlayerMP)player).connection.sendPacket(packet);
			}
		}
		
		return mapNum;
	}
	
	public int getBlockMapColor(BlockPos pos) {
		return world.getBlockState(pos).getMapColor(world, pos).colorIndex;
	}
	
	public static Object[] getRGBfromMapColor(int i) {
		int arg = i;
		int color = arg >> 2;
		int index = arg & 3;
					
		if(color >= 0 && color < 64) {
			MapColor mapColor = MapColor.COLORS[color];
			if(mapColor != null) {
				int rgbInt = mapColor.getMapColor(index);
				return obj((rgbInt >> 16) & 0xFF, (rgbInt >> 8) & 0xFF, rgbInt & 0xFF);
			}
		}
		return obj(0, 0, 0); 
	}
	
}
