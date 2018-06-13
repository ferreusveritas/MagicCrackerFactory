package com.ferreusveritas.mcf.tileentity;

import com.ferreusveritas.mcf.blocks.BlockCartographer;
import com.ferreusveritas.mcf.util.CommandManager;
import com.ferreusveritas.mcf.util.MethodDescriptor;

import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import net.minecraft.block.material.MapColor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.SPacketMaps;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapData;

public class TileCartographer extends TileEntity implements IPeripheral, ITickable  {
	
	int mapNum = 0;
	MapData currMapData;
	
	public MapData setCurrMapData(int mapNum) {
		this.mapNum = mapNum;
		this.currMapData = (MapData) world.loadData(MapData.class, "map_" + mapNum);
		return this.currMapData;
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
	
	public enum ComputerMethod {
		setMapNum("n", true, "mapNum"),
		getMapNum("n", true, "mapNum"),
		setMapPixel("nn", true, "x", "z"),
		getMapPixel("nn", false, "x", "z"),
		setMapCenter("nn", true, "x", "z"),
		getMapCenter("", false),
		setMapScale("n", true, "scale"),
		getMapScale("", false),
		setMapDimension("n", true, "dimension"),
		getMapDimension("", false),
		updateMap("", true),
		getBlockMapColor("nnn", false, "xCoord", "yCoord", "zCoord"),
		getRGBfromMapColor("n", false, "index");
		
		final MethodDescriptor md;
		private ComputerMethod(String argTypes, boolean cached, String ... args) { md = new MethodDescriptor(argTypes, cached, args); }
	}
	
	static CommandManager<ComputerMethod> commandManager = new CommandManager<>(ComputerMethod.class);
	
	@Override
	public void update() {
		
		BlockCartographer cartographer = (BlockCartographer)getBlockType();
		
		//Run commands that are cached that shouldn't be in the lua thread
		synchronized(commandManager.getCachedCommands()) {
			if(cartographer != null) {
				for(CommandManager<ComputerMethod>.CachedCommand cmd:  commandManager.getCachedCommands()) {
					switch(cmd.method) {
					case setMapNum: setCurrMapData(cmd.i()); break;
					case setMapPixel: setMapPixel(cmd.i(), cmd.i(), cmd.i()); break;
					case setMapCenter: getCurrMapData().xCenter = cmd.i(); getCurrMapData().zCenter = cmd.i(); break;
					case setMapScale: getCurrMapData().scale = (byte) MathHelper.clamp(cmd.i(), 0, 4);
					case setMapDimension: getCurrMapData().dimension = cmd.i(); break;
					case updateMap: updateMap(); break;
					default: break;
					}
				}
				commandManager.clear();
			}
		}
		
	}
	
	@Override
	public String getType() {
		return "cartographer";
	}
	
	@Override
	public String[] getMethodNames() {
		return commandManager.getMethodNames();
	}
	
	private int getInt(Object[] arguments, int arg) {
		return ((Double)arguments[arg]).intValue();
	}
	
	/**
	* I hear ya Dan!  Make the function threadsafe by caching the commmands to run in the main world server thread and not the lua thread.
	*/
	@Override
	public Object[] callMethod(IComputerAccess computer, ILuaContext context, int methodNum, Object[] arguments) throws LuaException {
		if(methodNum < 0 || methodNum >= commandManager.getNumMethods()) {
			throw new IllegalArgumentException("Invalid method number");
		}
		
		BlockCartographer cartographer = (BlockCartographer)getBlockType();
		World world = getWorld();
		
		if(!world.isRemote && cartographer != null) {
			ComputerMethod method = ComputerMethod.values()[methodNum];
			
			if(method.md.validateArguments(arguments)) {
				switch(method) {
					case getMapNum:
						return new Object[] { mapNum };
					case getMapPixel:
						return new Object[] { getMapPixel(getInt(arguments, 0), getInt(arguments, 1)) };
					case getMapCenter:
						return new Object[] { getCurrMapData().xCenter, getCurrMapData().zCenter };
					case getMapScale:
						return new Object[] { getCurrMapData().scale };
					case getMapDimension:
						return new Object[] { getCurrMapData().dimension };
					case getBlockMapColor:{
						BlockPos pos = new BlockPos(getInt(arguments, 0), getInt(arguments, 1), getInt(arguments, 2));
						return new Object[] { world.getBlockState(pos).getMapColor(world, pos).colorIndex };
						}
					case getRGBfromMapColor:{
						int arg = getInt(arguments, 0);
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
					}
					default:
						if(method.md.isCached()) {
							commandManager.cacheCommand(methodNum, arguments);
						}
				}
			}
		}
		
		return null;
	}
	
	@Override
	public boolean equals(IPeripheral other) {
		return this == other;
	}
	
}
