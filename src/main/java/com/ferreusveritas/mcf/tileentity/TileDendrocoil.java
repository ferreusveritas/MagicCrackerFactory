package com.ferreusveritas.mcf.tileentity;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ferreusveritas.dynamictrees.api.TreeHelper;
import com.ferreusveritas.dynamictrees.api.TreeRegistry;
import com.ferreusveritas.dynamictrees.api.treedata.ITreePart;
import com.ferreusveritas.dynamictrees.blocks.BlockRooty;
import com.ferreusveritas.dynamictrees.trees.Species;
import com.ferreusveritas.dynamictrees.util.SafeChunkBounds;
import com.ferreusveritas.dynamictrees.worldgen.JoCode;
import com.ferreusveritas.mcf.ModConstants;
import com.ferreusveritas.mcf.blocks.BlockPeripheral;
import com.ferreusveritas.mcf.util.CommandManager;
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
		growPulse("", true),
		getCode("", false),
		setCode("ss", true, "treeName", "joCode"),
		getTree("", false),
		plantTree("s", true, "treeName"),
		killTree("", true),
		getSoilLife("", false),
		setSoilLife("n", true, "life"),
		getSpeciesList("", false),
		createStaff("sssb", true, "treeName", "joCode", "rgbColor", "readOnly");
		
		final MethodDescriptor md;
		private ComputerMethod(String argTypes, boolean cached, String ... args) { md = new MethodDescriptor(argTypes, cached, args); }
	}
	
	static CommandManager<ComputerMethod> commandManager = new CommandManager<>(ComputerMethod.class);
	
	private String treeName;
	private int soilLife;
	
	@Override
	public void update() {
		
		BlockPeripheral dendroCoil = (BlockPeripheral)getBlockType();
		World world = getWorld();
		
		synchronized(this) {
			treeName = new String(getSpecies(world, getPos()));
			soilLife = getSoilLife(world, getPos());
		}
		
		//Run commands that are cached that shouldn't be in the lua thread
		synchronized(commandManager) {
			if(dendroCoil != null) {
				for(CommandManager<ComputerMethod>.CachedCommand cmd:  commandManager.getCachedCommands()) {
					switch(cmd.method) {
						case growPulse: growPulse(world, getPos()); break;
						case killTree: killTree(world, getPos()); break;
						case plantTree: plantTree(world, getPos(), cmd.s()); break;
						case setCode: setCode(world, getPos(), cmd.s(), cmd.s()); break;
						case setSoilLife: setSoilLife(world, getPos(), cmd.i()); break;
						case createStaff: createStaff(world, getPos(), cmd.s(), cmd.s(), cmd.s(), cmd.b()); break;
						default: break;
					}
				}
				commandManager.clear();
			}
		}
		
	}
	
	@Override
	public String getType() {
		return "dendrocoil";
	}
	
	@Override
	public String[] getMethodNames() {
		return commandManager.getMethodNames();
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
						return new Object[]{ getCode(world, getPos()) };
					case getTree:
						synchronized(this) {
							return new Object[]{treeName};
						}
					case getSoilLife:
						synchronized(this) {
							return new Object[]{soilLife};
						}
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
		pos = pos.up();
		if(TreeHelper.isRooty(world.getBlockState(pos))) {
			return new JoCode().buildFromTree(world, pos).toString();
		}
		
		return "";
	}
	
	private static void setCode(World world, BlockPos pos, String treeName, String JoCode) {
		Species species = TreeRegistry.findSpeciesSloppy(treeName);
		JoCode jo = species.getJoCode(JoCode);
		if(species != Species.NULLSPECIES) {
			jo.setCareful(true).generate(world, species, pos.up(), world.getBiome(pos), EnumFacing.NORTH, 8, SafeChunkBounds.ANY);
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
		IBlockState rootyState = world.getBlockState(pos.up());
		ITreePart part = TreeHelper.getTreePart(rootyState);
		if(part.isRootNode()) {
			return TreeHelper.getExactSpecies(rootyState, world, pos.up()).toString();
		}
		
		return "";
	}

	private static void plantTree(World world, BlockPos pos, String treeName) {
		Species species = TreeRegistry.findSpeciesSloppy(treeName);
		species.plantSapling(world, pos.up(2));
	}

	private static void growPulse(World world, BlockPos pos) {
		ITreePart part = TreeHelper.getTreePart(world.getBlockState(pos.up()));
		if(part.isRootNode()) {
			TreeHelper.growPulse(world, pos.up());
		}
	}
	
	private static void killTree(World world, BlockPos pos) {
		ITreePart part = TreeHelper.getTreePart(world.getBlockState(pos.up()));
		if(part.isRootNode()) {
			((BlockRooty)part).destroyTree(world, pos.up());
		}
	}

	private static int getSoilLife(World world, BlockPos pos) {
		IBlockState rootyState = world.getBlockState(pos.up());
		BlockRooty rooty = TreeHelper.getRooty(rootyState);
		if(rooty != null) {
			return rooty.getSoilLife(rootyState, world, pos.up());
		}
		return 0;
	}

	private static void setSoilLife(World world, BlockPos pos, int life) {
		ITreePart part = TreeHelper.getTreePart(world.getBlockState(pos.up()));
		if(part.isRootNode()) {
			((BlockRooty)part).setSoilLife(world, pos.up(), life);
		}
	}
	
	@Override
	public boolean equals(IPeripheral other) {
		return this == other;
	}
	
}
