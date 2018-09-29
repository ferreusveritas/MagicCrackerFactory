package com.ferreusveritas.mcf.tileentity;

import java.util.Arrays;
import java.util.Map;
import java.util.Map.Entry;

import com.ferreusveritas.mcf.util.CommandManager;
import com.ferreusveritas.mcf.util.MethodDescriptor;
import com.ferreusveritas.mcf.util.MethodDescriptor.MethodDescriptorProvider;
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
	
	public enum ComputerMethod implements MethodDescriptorProvider {
		getMapPixel       	("nnn" ,	"mapNum,x,z"           ,	true ,	(world, peri, args) -> obj(getTool(peri).getMapPixel(args.i(0), args.i(1), args.i(2))) ),
		setMapPixel       	("nnnn",	"mapNum,x,z,colorIndex",	false,	(world, peri, args) -> obj(getTool(peri).setMapPixel(args.i(0), args.i(1), args.i(2), args.i(3))) ),
		getMapPixels      	("n"   ,	"mapNum"               ,	true ,	(world, peri, args) -> getTool(peri).getMapPixels(args.i(0)) ),
		setMapPixels      	("ns"  ,	"mapNum,array"         ,	false,	(world, peri, args) -> obj(getTool(peri).setMapPixels(args.i(0), args.s(1))) ),
		getMapCenter      	("n"   ,	"mapNum"               ,	true ,	(world, peri, args) -> obj(getTool(peri).getMapData(args.i(0)).xCenter, getTool(peri).getMapData(args.i(0)).zCenter ) ),
		setMapCenter      	("nnn" ,	"mapNum,x,z"           ,	false,	(world, peri, args) -> obj(getTool(peri).setMapCenter(args.i(0), args.i(1), args.i(2))) ),
		getMapScale       	("n"   ,	"mapNum"               ,	true ,	(world, peri, args) -> obj(getTool(peri).getMapData(args.i(0)).scale) ),
		setMapScale       	("nn"  ,	"mapNum,scale"         ,	false,	(world, peri, args) -> obj(getTool(peri).getMapData(args.i(0)).scale = (byte) MathHelper.clamp(args.i(1), 0, 4)) ),
		getMapDimension   	("n"   ,	"mapNum"               ,	true ,	(world, peri, args) -> obj(getTool(peri).getMapData(args.i(0)).dimension) ),
		setMapDimension   	("nn"  ,	"mapNum,dimension"     ,	false,	(world, peri, args) -> obj(getTool(peri).getMapData(args.i(0)).dimension = args.i(1)) ),
		copyMapData       	("nn"  ,	"mapA,mapB"            ,	false,	(world, peri, args) -> obj(getTool(peri).copyMapData(args.i(0), args.i(1))) ),
		swapMapData       	("nn"  ,	"mapA,mapB"            ,	false,	(world, peri, args) -> obj(getTool(peri).swapMapData(args.i(0), args.i(1))) ),
		updateMap         	("n"   ,	""                     ,	false,	(world, peri, args) -> obj(getTool(peri).updateMap(args.i(0))) ),
		getBlockMapColor  	("nnn" ,	"x,y,z"                ,	true ,	(world, peri, args) -> obj(getTool(peri).getBlockMapColor(args.p())) ),
		getRGBfromMapColor	("n"   ,	"index"                ,	true ,	(world, peri, args) -> getRGBfromMapColor(args.i()) );
		
		final MethodDescriptor md;
		private ComputerMethod(String argTypes, String args, boolean synced, SyncProcess process) { md = new MethodDescriptor(toString(), argTypes, args, process, synced); }
		
		public static TileCartographer getTool(MCFPeripheral peripheral) {
			return (TileCartographer) peripheral;
		}
		
		@Override
		public MethodDescriptor getMethodDescriptor() {
			return md;
		}
		
	}
	
	static CommandManager<ComputerMethod> commandManager = new CommandManager<>(ComputerMethod.class);
	
	@Override
	public CommandManager getCommandManager() {
		return commandManager;
	}
	
	//Cache the last accessed map data for efficiency
	private int lastMapNum = -1;
	private MapData lastMapData = null;
	
	protected MapData getMapData(int mapNum) {
		if(lastMapData != null && lastMapNum == mapNum) {
			return lastMapData;
		} else {
			lastMapNum = mapNum;
			lastMapData = (MapData) world.loadData(MapData.class, "map_" + mapNum);
			return lastMapData;	
		}
	}
	
	public int getMapPixel(int mapNum, int x, int z) {
		if(x >= 0 && x < 128 && z >= 0 && z < 128) {
			return getMapData(mapNum).colors[x + z * 128];
		}
		return 0;
	}
	
	public Object[] getMapPixels(int mapNum) {
		byte colors[] = getMapData(mapNum).colors;
		byte[] pixels = Arrays.copyOf( colors, colors.length );
		return new Object[] { pixels };
	}
	
	public int setMapPixel(int mapNum, int x, int z, int colorFull) {
		if(x >= 0 && x < 128 && z >= 0 && z < 128) {
			getMapData(mapNum).colors[x + z * 128] = (byte) (colorFull >= 0 && colorFull <= (51 * 4) ? colorFull : 0);
		}
		return 0;
	}
	
	public void setMapPixel(int mapNum, int x, int z, int color52, int index4) {
		setMapPixel(mapNum, x, z, color52 * 4 | index4);
	}
	
	public int setMapPixels(int mapNum, String data) {
		char[] charArray = data.toCharArray();
		byte[] byteArray = getMapData(mapNum).colors;
		
		if(charArray.length == 128 * 128) {
			for(int i = 0; i < charArray.length; i++) {
				char val = charArray[i];
				byteArray[i] = val <= 207 ? (byte) val : 0;
			}
		}
		
		return 0;
	}
	
	public int setMapCenter(int mapNum, int x, int z) {
		MapData mapData = getMapData(mapNum);
		mapData.xCenter = x;
		mapData.zCenter = z;
		return 0;
	}
	
	public int copyMapData(int mapSrc, int mapDst) {
		
		MapData mapDataSrc = getMapData(mapSrc);
		MapData mapDataDst = getMapData(mapDst);
		
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
		
		return 0;
	}
	
	public int swapMapData(int mapNumA, int mapNumB) {
		
		MapData mapDataA = getMapData(mapNumA);
		MapData mapDataB = getMapData(mapNumB);
		
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
			
		return 0;
	}
	
	public int updateMap(int mapNum) {
		MapData mapData = getMapData(mapNum);
		mapData.markDirty();//Mark as dirty so the changes save to disk
		Packet<?> packet = new SPacketMaps(mapNum, mapData.scale, mapData.trackingPosition, mapData.mapDecorations.values(), mapData.colors, 0, 0, 128, 128);
		
		for(EntityPlayer player : world.playerEntities) {
			if(player instanceof EntityPlayerMP) {
				((EntityPlayerMP)player).connection.sendPacket(packet);
			}
		}
		
		return 0;
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
