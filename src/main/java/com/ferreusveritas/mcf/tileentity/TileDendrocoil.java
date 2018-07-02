package com.ferreusveritas.mcf.tileentity;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ferreusveritas.dynamictrees.api.TreeHelper;
import com.ferreusveritas.dynamictrees.api.TreeRegistry;
import com.ferreusveritas.dynamictrees.api.treedata.ITreePart;
import com.ferreusveritas.dynamictrees.trees.Species;
import com.ferreusveritas.dynamictrees.util.SafeChunkBounds;
import com.ferreusveritas.dynamictrees.worldgen.JoCode;
import com.ferreusveritas.mcf.ModConstants;
import com.ferreusveritas.mcf.blocks.BlockPeripheral;
import com.ferreusveritas.mcf.util.CommandManager;
import com.ferreusveritas.mcf.util.CommandManager.CachedCommand;
import com.ferreusveritas.mcf.util.MethodDescriptor;

import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TileDendrocoil extends TileEntity implements IPeripheral, ITickable {
	
	public enum ComputerMethod {
		growPulse("nnn", true, "x", "y", "z"),
		getCode("nnn", false, "x", "y", "z"),
		setCode("nnnss", true, "x", "y", "z", "treeName", "joCode"),
		getTree("nnn", false, "x", "y", "z"),
		plantTree("nnns", true, "x", "y", "z", "treeName"),
		killTree("nnn", true, "x", "y", "z"),
		getSoilLife("nnn", false, "x", "y", "z"),
		setSoilLife("nnnn", true, "x", "y", "z", "life"),
		getSpeciesList("", false),
		createStaff("sssb", true, "treeName", "joCode", "rgbColor", "readOnly");
		
		final MethodDescriptor md;
		private ComputerMethod(String argTypes, boolean cached, String ... args) { md = new MethodDescriptor(argTypes, cached, args); }
	}
	
	static CommandManager<ComputerMethod> commandManager = new CommandManager<>(ComputerMethod.class);
	
	@Override
	public void update() {
		
		BlockPeripheral dendroCoil = (BlockPeripheral)getBlockType();
		World world = getWorld();
		
		//Run commands that are cached that shouldn't be in the lua thread
		synchronized(commandManager) {
			if(dendroCoil != null) {
				for(CommandManager<ComputerMethod>.CachedCommand cmd:  commandManager.getCachedCommands()) {
					switch(cmd.method) {
						case growPulse: growPulse(world, getCmdPos(cmd)); break;
						case killTree: killTree(world, getCmdPos(cmd)); break;
						case plantTree: plantTree(world, getCmdPos(cmd), cmd.s()); break;
						case setCode: setCode(world, getCmdPos(cmd), cmd.s(), cmd.s()); break;
						case setSoilLife: setSoilLife(world, getCmdPos(cmd), cmd.i()); break;
						case createStaff: createStaff(world, getPos(), cmd.s(), cmd.s(), cmd.s(), cmd.b()); break;
						default: break;
					}
				}
				commandManager.clear();
			}
		}
		
	}
	
	public BlockPos getCmdPos(CachedCommand cmd) {
		return new BlockPos(cmd.i(), cmd.i(), cmd.i());
	}
	
	@Override
	public String getType() {
		return "dendrocoil";
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
		
		BlockPeripheral dendroCoil = (BlockPeripheral)getBlockType();
		World world = getWorld();
		
		if(!world.isRemote && dendroCoil != null) {
			ComputerMethod method = ComputerMethod.values()[methodNum];
			
			if(method.md.validateArguments(arguments)) {
				switch(method) {
					case getCode:
						return new Object[]{ getCode(world, new BlockPos(getInt(arguments, 0), getInt(arguments, 1), getInt(arguments, 2))) };
					case getTree:
						String treeName = new String(getSpecies(world, new BlockPos(getInt(arguments, 0), getInt(arguments, 1), getInt(arguments, 2))));
						return new Object[]{treeName};
					case getSoilLife:
						int soilLife = getSoilLife(world, new BlockPos(getInt(arguments, 0), getInt(arguments, 1), getInt(arguments, 2)));
						return new Object[]{soilLife};
					case getSpeciesList:
						ArrayList<String> species = new ArrayList<String>();
						TreeRegistry.getSpeciesDirectory().forEach(r -> species.add(r.toString()));
						return species.toArray();
					default:
						if(method.md.isCached()) {
							synchronized (commandManager) {
								commandManager.cacheCommand(methodNum, arguments);
							}
						}
				}
			}
		}
		
		return null;
	}
	
	private static String getCode(World world, BlockPos pos) {
		BlockPos rootPos = TreeHelper.findRootNode(world.getBlockState(pos), world, pos);
		if(rootPos != BlockPos.ORIGIN) {
			return new JoCode().buildFromTree(world, rootPos).toString();
		}
		return "";
	}
	
	private static void setCode(World world, BlockPos rootPos, String treeName, String JoCode) {
		Species species = TreeRegistry.findSpeciesSloppy(treeName);
		if(species != Species.NULLSPECIES) {
			species.getJoCode(JoCode).setCareful(true).generate(world, species, rootPos, world.getBiome(rootPos), EnumFacing.NORTH, 8, SafeChunkBounds.ANY);
		} else {
			Logger.getLogger(ModConstants.MODID).log(Level.WARNING, "Tree: " + treeName + " not found.");
		}
	}
	
	private static void createStaff(World world, BlockPos pos, String treeName, String JoCode, String rgb, boolean readOnly) {
		ItemStack stack = new ItemStack(com.ferreusveritas.dynamictrees.ModItems.treeStaff, 1, 0);
		Species species = TreeRegistry.findSpeciesSloppy(treeName);
		com.ferreusveritas.dynamictrees.ModItems.treeStaff.setSpecies(stack, species).setCode(stack, JoCode).setColor(stack, rgb).setReadOnly(stack, readOnly);
		EntityItem entityItem = new EntityItem(world, pos.getX() + 0.5, pos.getY() + 1.5, pos.getZ() + 0.5, stack);
		entityItem.motionX = 0;
		entityItem.motionY = 0;
		entityItem.motionZ = 0;
		world.spawnEntity(entityItem);
	}
	
	private static String getSpecies(World world, BlockPos pos) {
		Species species = TreeHelper.getExactSpecies(world.getBlockState(pos), world, pos);
		if(species != Species.NULLSPECIES) {
			return species.toString();
		}
		
		return "";
	}
	
	private static void plantTree(World world, BlockPos pos, String treeName) {
		Species species = TreeRegistry.findSpeciesSloppy(treeName);
		species.plantSapling(world, pos);
	}
	
	private static void growPulse(World world, BlockPos rootPos) {
		ITreePart part = TreeHelper.getTreePart(world.getBlockState(rootPos));
		if(part.isRootNode()) {
			TreeHelper.growPulse(world, rootPos);
		}
	}
	
	private static void killTree(World world, BlockPos pos) {
		BlockPos rootPos = TreeHelper.findRootNode(world.getBlockState(pos), world, pos);
		if(rootPos != BlockPos.ORIGIN) {
			TreeHelper.getRooty(world.getBlockState(rootPos)).destroyTree(world, rootPos);
		}
	}
	
	private static int getSoilLife(World world, BlockPos pos) {
		BlockPos rootPos = TreeHelper.findRootNode(world.getBlockState(pos), world, pos);
		if(rootPos != BlockPos.ORIGIN) {
			IBlockState state = world.getBlockState(rootPos);
			return TreeHelper.getRooty(state).getSoilLife(state, world, rootPos);
		}
		return 0;
	}
	
	private static void setSoilLife(World world, BlockPos pos, int life) {
		BlockPos rootPos = TreeHelper.findRootNode(world.getBlockState(pos), world, pos);
		if(rootPos != BlockPos.ORIGIN) {
			IBlockState state = world.getBlockState(rootPos);
			TreeHelper.getRooty(state).setSoilLife(world, rootPos, life);
		}
	}
	
	@Override
	public boolean equals(IPeripheral other) {
		return this == other;
	}
	
}
