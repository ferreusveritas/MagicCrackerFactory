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
import com.ferreusveritas.mcf.util.CommandManager;
import com.ferreusveritas.mcf.util.MethodDescriptor;
import com.ferreusveritas.mcf.util.MethodDescriptor.MethodDescriptorProvider;
import com.ferreusveritas.mcf.util.MethodDescriptor.SyncProcess;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TileDendrocoil extends MCFPeripheral {
	
	public TileDendrocoil() {
		super("dendrocoil");
	}
	
	public enum ComputerMethod implements MethodDescriptorProvider {
		growPulse("nnn", "x, y, z", (world, peri, args) -> obj(growPulse(world, args.p())) ),
		getCode("nnn", "x, y, z", (world, peri, args) -> obj(getCode(world, args.p())) ),
		setCode("nnnss", "x, y, z, treeName, joCode", (world, peri, args) -> obj(setCode(world, args.p(0), args.s(3), args.s(4))) ),
		getSpecies("nnn", "x, y, z", (world, peri, args) -> obj(getSpecies(world, args.p())) ),
		plantTree("nnns", "x, y, z, treeName", (world, peri, args) -> obj(plantTree(world, args.p(0), args.s(3))) ),
		killTree("nnn", "x, y, z", (world, peri, args) -> obj(killTree(world, args.p())) ),
		getSoilLife("nnn", "x, y, z", (world, peri, args) -> obj(getSoilLife(world, args.p())) ),
		setSoilLife("nnnn", "x, y, z, life", (world, peri, args) -> obj(setSoilLife(world, args.p(), args.i(3))) ),
		getSpeciesList("", "", (world, peri, args) -> getSpeciesList() ), 
		createStaff("sssb", "x, y, z, treeName, joCode, rgbColor, readOnly", (world, peri, args) -> obj(createStaff(world, args.p(0), args.s(3), args.s(4), args.s(5), args.b(6))) );
		
		final MethodDescriptor md;
		private ComputerMethod(String argTypes, String args, SyncProcess process) { md = new MethodDescriptor(argTypes, args, process); }
		
		public static TileSentinel getTool(MCFPeripheral peripheral) {
			return (TileSentinel) peripheral;
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
	
	private static String getCode(World world, BlockPos pos) {
		BlockPos rootPos = TreeHelper.findRootNode(world.getBlockState(pos), world, pos);
		if(rootPos != BlockPos.ORIGIN) {
			return new JoCode().buildFromTree(world, rootPos).toString();
		}
		return "";
	}
	
	private static int setCode(World world, BlockPos rootPos, String treeName, String JoCode) {
		Species species = TreeRegistry.findSpeciesSloppy(treeName);
		if(species != Species.NULLSPECIES) {
			species.getJoCode(JoCode).setCareful(true).generate(world, species, rootPos, world.getBiome(rootPos), EnumFacing.NORTH, 8, SafeChunkBounds.ANY);
		} else {
			Logger.getLogger(ModConstants.MODID).log(Level.WARNING, "Tree: " + treeName + " not found.");
		}
		return 0;
	}
	
	private static int createStaff(World world, BlockPos pos, String treeName, String JoCode, String rgb, boolean readOnly) {
		ItemStack stack = new ItemStack(com.ferreusveritas.dynamictrees.ModItems.treeStaff, 1, 0);
		Species species = TreeRegistry.findSpeciesSloppy(treeName);
		com.ferreusveritas.dynamictrees.ModItems.treeStaff.setSpecies(stack, species).setCode(stack, JoCode).setColor(stack, rgb).setReadOnly(stack, readOnly);
		EntityItem entityItem = new EntityItem(world, pos.getX() + 0.5, pos.getY() + 1.5, pos.getZ() + 0.5, stack);
		entityItem.motionX = 0;
		entityItem.motionY = 0;
		entityItem.motionZ = 0;
		world.spawnEntity(entityItem);
		return 0;
	}
	
	private static String getSpecies(World world, BlockPos pos) {
		Species species = TreeHelper.getExactSpecies(world.getBlockState(pos), world, pos);
		if(species != Species.NULLSPECIES) {
			return species.toString();
		}
		
		return "";
	}
	
	private static int plantTree(World world, BlockPos pos, String treeName) {
		Species species = TreeRegistry.findSpeciesSloppy(treeName);
		species.plantSapling(world, pos);
		return 0;
	}
	
	private static int growPulse(World world, BlockPos rootPos) {
		ITreePart part = TreeHelper.getTreePart(world.getBlockState(rootPos));
		if(part.isRootNode()) {
			TreeHelper.growPulse(world, rootPos);
		}
		return 0;
	}
	
	private static int killTree(World world, BlockPos pos) {
		BlockPos rootPos = TreeHelper.findRootNode(world.getBlockState(pos), world, pos);
		if(rootPos != BlockPos.ORIGIN) {
			TreeHelper.getRooty(world.getBlockState(rootPos)).destroyTree(world, rootPos);
		}
		return 0;
	}
	
	private static int getSoilLife(World world, BlockPos pos) {
		BlockPos rootPos = TreeHelper.findRootNode(world.getBlockState(pos), world, pos);
		if(rootPos != BlockPos.ORIGIN) {
			IBlockState state = world.getBlockState(rootPos);
			return TreeHelper.getRooty(state).getSoilLife(state, world, rootPos);
		}
		return 0;
	}
	
	private static int setSoilLife(World world, BlockPos pos, int life) {
		BlockPos rootPos = TreeHelper.findRootNode(world.getBlockState(pos), world, pos);
		if(rootPos != BlockPos.ORIGIN) {
			IBlockState state = world.getBlockState(rootPos);
			TreeHelper.getRooty(state).setSoilLife(world, rootPos, life);
		}
		return life;
	}

	private static String[] getSpeciesList() {
		ArrayList<String> species = new ArrayList<String>();
		TreeRegistry.getSpeciesDirectory().forEach(r -> species.add(r.toString()));
		return species.toArray(new String[0]);
	}

	
}
