package com.ferreusveritas.mcf.tileentity;

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

public class TileCartographer extends MCFPeripheral  {
	
	public TileCartographer() {
		super("cartographer");
	}

	public enum ComputerMethod {
		setMapNum("n", "mapNum",
			(world, peri, args) -> {
				getTool(peri).setCurrMapData(getInt(args, 0));
				return new Object[0];
			}),
		
		getMapNum("n", "mapNum",
			(world, peri, args) -> {
				return new Object[] { getTool(peri).getMapNum() };
			}),
		
		setMapPixel("nnn", "x, z, colorIndex",
			(world, peri, args) -> {
				getTool(peri).setMapPixel(getInt(args, 0), getInt(args, 1), getInt(args, 2));
				return new Object[0];
			}),
		
		getMapPixel("nn", "x, z",
			(world, peri, args) -> {
				return new Object[] { getTool(peri).getMapPixel(getInt(args, 0), getInt(args, 1)) };
			}),
		
		setMapCenter("nn", "x, z",
			(world, peri, args) -> {
				getTool(peri).getCurrMapData().xCenter = getInt(args, 0);
				getTool(peri).getCurrMapData().zCenter = getInt(args, 1);
				return new Object[0];
			}),
		
		getMapCenter("", "",
			(world, peri, args) -> {
				return new Object[] { getTool(peri).getCurrMapData().xCenter, getTool(peri).getCurrMapData().zCenter };
			}),
		
		setMapScale("n", "scale",
			(world, peri, args) -> {
				getTool(peri).getCurrMapData().scale = (byte) MathHelper.clamp(getInt(args, 0), 0, 4);
				return new Object[0];
			}),
		
		getMapScale("", "",
			(world, peri, args) -> {
				return new Object[] { getTool(peri).getCurrMapData().scale };
			}),
		
		setMapDimension("n", "dimension",
			(world, peri, args) -> {
				getTool(peri).getCurrMapData().dimension = getInt(args, 0);
				return new Object[0];
			}),
		
		getMapDimension("", "",
			(world, peri, args) -> {
				return new Object[] { getTool(peri).getCurrMapData().dimension };
			}),
		
		updateMap("", "",
			(world, peri, args) -> {
				getTool(peri).updateMap();
				return new Object[0];
			}),
		
		getBlockMapColor("nnn", "xCoord, yCoord, zCoord",
			(world, peri, args) -> {
				BlockPos pos = new BlockPos(getInt(args, 0), getInt(args, 1), getInt(args, 2));
				return new Object[] { world.getBlockState(pos).getMapColor(world, pos).colorIndex };
			}),
		
		getRGBfromMapColor("n", "index",
			(world, peri, args) -> {
				int arg = getInt(args, 0);
				int color = arg >> 2;
				int index = arg & 3;
					
				if(color >= 0 && color < 64) {
					MapColor mapColor = MapColor.COLORS[color];
					if(mapColor != null) {
						int rgbInt = mapColor.getMapColor(index);
						return new Object[] { (rgbInt >> 16) & 0xFF, (rgbInt >> 8) & 0xFF, rgbInt & 0xFF };
					}
				}
				return new Object[] { 0, 0, 0 }; 
			});
		
		
		final MethodDescriptor md;
		private ComputerMethod(String argTypes, String args, SyncProcess process) { md = new MethodDescriptor(argTypes, args, process); }
		
		public static TileCartographer getTool(MCFPeripheral peripheral) {
			return (TileCartographer) peripheral;
		}
		
		public static int getInt(Object[] args, int arg) {
			return ((Double)args[arg]).intValue();
		}
	}
	
	static CommandManager<ComputerMethod> commandManager = new CommandManager<>(ComputerMethod.class);
	
	@Override
	public CommandManager getCommandManager() {
		return commandManager;
	}
	
	int mapNum = 0;
	MapData currMapData;
	
	public MapData setCurrMapData(int mapNum) {
		this.mapNum = mapNum;
		this.currMapData = (MapData) world.loadData(MapData.class, "map_" + mapNum);
		return this.currMapData;
	}
	
	public int getMapNum() {
		return mapNum;
	}
	
	public MapData getCurrMapData() {
		return currMapData != null ? currMapData : setCurrMapData(0);
	}
	
	public void setMapPixel(int x, int z, int color52, int index4) {
		setMapPixel(x, z, color52 * 4 | index4);
	}
	
	public int getMapPixel(int x, int z) {
		if(x >= 0 && x < 128 && z >= 0 && z < 128) {
			return getCurrMapData().colors[x + z * 128];
		}
		return 0;
	}
	
	public void setMapPixel(int x, int z, int colorFull) {
		if(x >= 0 && x < 128 && z >= 0 && z < 128) {
			getCurrMapData().colors[x + z * 128] = (byte) (colorFull >= 0 && colorFull <= (51 * 4) ? colorFull : 0);
		}
	}
	
	public void updateMap() {
		MapData mapData = getCurrMapData();
		mapData.markDirty();//Mark as dirty so the changes save to disk
		Packet<?> packet = new SPacketMaps(mapNum, mapData.scale, mapData.trackingPosition, mapData.mapDecorations.values(), mapData.colors, 0, 0, 128, 128);

		for(EntityPlayer player : world.playerEntities) {
			if(player instanceof EntityPlayerMP) {
				((EntityPlayerMP)player).connection.sendPacket(packet);
			}
		}
	}
	
}
