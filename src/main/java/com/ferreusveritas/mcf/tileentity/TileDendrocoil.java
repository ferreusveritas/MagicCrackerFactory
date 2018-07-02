package com.ferreusveritas.mcf.tileentity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ferreusveritas.dynamictrees.ModConstants;
import com.ferreusveritas.dynamictrees.ModItems;
import com.ferreusveritas.dynamictrees.api.TreeHelper;
import com.ferreusveritas.dynamictrees.api.TreeRegistry;
import com.ferreusveritas.dynamictrees.api.treedata.ITreePart;
import com.ferreusveritas.dynamictrees.blocks.BlockRooty;
import com.ferreusveritas.dynamictrees.trees.Species;
import com.ferreusveritas.dynamictrees.util.SafeChunkBounds;
import com.ferreusveritas.dynamictrees.worldgen.JoCode;
import com.ferreusveritas.mcf.blocks.BlockPeripheral;

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
import net.minecraft.world.biome.Biome;

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
		createStaff("sssb", true, "treeName", "joCode", "rgbColor", "readOnly"),
		//testPoisson("nnn", true, "radius1", "radius2", "angle"),
		//testPoisson2("nnnn", true, "radius1", "radius2", "angle", "radius3", "onlyTight"),
		//testPoisson3("nnnnnb", true, "radius1", "delX", "delZ", "radius2", "radius3", "onlyTight"),
		getBiome("nn", false, "xCoord", "zCoord");
		
		private final String argTypes;
		private final String args[];
		private final boolean cached;

		private ComputerMethod(String argTypes, boolean cached, String ... args) {
			this.argTypes = argTypes;
			this.args = args;
			this.cached = cached;
		}
		
		public boolean isCached() {
			return cached;
		}
		
		public boolean isValidArguments(Object[] arguments) {
			if(arguments.length >= argTypes.length()) {
				for (int i = 0; i < argTypes.length(); i++){
					if(!CCDataType.byIdent(argTypes.charAt(i)).isInstance(arguments[i])) {
						return false;
					}
				}
				return true;
			}
			return false;
		}
		
		public boolean validateArguments(Object[] arguments) throws LuaException {
			if(isValidArguments(arguments)) {
				return true;
			}
			throw new LuaException(invalidArgumentsError());
		}
		
		public String invalidArgumentsError() {
			String error = "Expected: " + this.toString();
			for (int i = 0; i < argTypes.length(); i++){
				error += " " + args[i] + "<" + CCDataType.byIdent(argTypes.charAt(i)).name + ">";
			}
			return error;
		}
	}
	
	private class CachedCommand {
		ComputerMethod method;
		Object[] arguments;
		int argRead = 0;
		
		public CachedCommand(int method, Object[] args) {
			this.method = ComputerMethod.values()[method];
			this.arguments = args;
		}
		
		/*public double d() {
			return ((Double)arguments[argRead++]).doubleValue();
		}*/
		
		public int i() {
			return ((Double)arguments[argRead++]).intValue();
		}
		
		public String s() {
			return ((String)arguments[argRead++]);
		}
		
		public boolean b() {
			return ((Boolean)arguments[argRead++]).booleanValue();
		}
	}
	
	private ArrayList<CachedCommand> cachedCommands = new ArrayList<CachedCommand>(1);
	private String treeName;
	private int soilLife;

	//Dealing with multithreaded biome requests
	BiomeRequest biomeRequest = null;
	
	public static final int numMethods = ComputerMethod.values().length;
	public static final String[] methodNames = new String[numMethods]; 
	static {
		for(ComputerMethod method : ComputerMethod.values()) { 
			methodNames[method.ordinal()] = method.toString(); 
		}
	}
	
	public void cacheCommand(int method, Object[] args) {
		synchronized (cachedCommands) {
			cachedCommands.add(new CachedCommand(method, args));
		}
	}
	
	@Override
	public void update() {
		
		BlockPeripheral dendroCoil = (BlockPeripheral)getBlockType();
		World world = getWorld();
		
		synchronized(this) {
			treeName = new String(getSpecies(world, getPos()));
			soilLife = getSoilLife(world, getPos());
		}
		
		//Run commands that are cached that shouldn't be in the lua thread
		synchronized(cachedCommands) {
			if(dendroCoil != null) {
				if(cachedCommands.size() > 0) { 
					for(CachedCommand cmd:  cachedCommands) {
						switch(cmd.method) {
							case growPulse: growPulse(world, getPos()); break;
							case killTree: killTree(world, getPos()); break;
							case plantTree: plantTree(world, getPos(), cmd.s()); break;
							case setCode: setCode(world, getPos(), cmd.s(), cmd.s()); break;
							case setSoilLife: setSoilLife(world, getPos(), cmd.i()); break;
							case createStaff: createStaff(world, getPos(), cmd.s(), cmd.s(), cmd.s(), cmd.b()); break;
							//case testPoisson: dendroCoil.testPoisson(world, getPos(), cmd.i(), cmd.i(), cmd.d(), cmd.b()); break;
							//case testPoisson2: dendroCoil.testPoisson2(world, getPos(), cmd.i(), cmd.i(), cmd.d(), cmd.i(), cmd.b()); break;
							//case testPoisson3: dendroCoil.testPoisson3(world, getPos(), cmd.i(), getPos().add(cmd.i(), 0, cmd.i()), cmd.i(), cmd.i()); break;
							default: break;
						}
					}
					cachedCommands.clear();
				}
			}
		}
		
		//Fulfill data requests
		if(biomeRequest != null) {
			biomeRequest.process(world);
		}
	}
	
	@Override
	public String getType() {
		return "dendrocoil";
	}
	
	@Override
	public String[] getMethodNames() {
		return methodNames;
	}
	
	/**
	* I hear ya Dan!  Make the function threadsafe by caching the commmands to run in the main world server thread and not the lua thread.
	*/
	@Override
	public Object[] callMethod(IComputerAccess computer, ILuaContext context, int methodNum, Object[] arguments) throws LuaException {
		if(methodNum < 0 || methodNum >= numMethods) {
			throw new IllegalArgumentException("Invalid method number");
		}
		
		BlockPeripheral dendroCoil = (BlockPeripheral)getBlockType();
		World world = getWorld();
		
		if(!world.isRemote && dendroCoil != null) {
			ComputerMethod method = ComputerMethod.values()[methodNum];

			if(method.validateArguments(arguments)) {
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
					case getBiome:
						if( (arguments[0] instanceof Double) &&
							(arguments[1] instanceof Double) &&
							(arguments[2] instanceof Double) &&
							(arguments[3] instanceof Double) &&
							(arguments[4] instanceof Double) ) {
							int xPosStart = ((Double)arguments[0]).intValue();
							int zPosStart = ((Double)arguments[1]).intValue();
							int xPosEnd = ((Double)arguments[2]).intValue();
							int zPosEnd = ((Double)arguments[3]).intValue();
							int step = ((Double)arguments[4]).intValue();
							
							biomeRequest = new BiomeRequest(
								new BlockPos(xPosStart, 0, zPosStart),
								new BlockPos(xPosEnd, 0, zPosEnd),
								step);
					
							Map<Integer, String> biomeNames = new HashMap<>();
							Map<Integer, Integer> biomeIds = new HashMap<>();
							
							int i = 1;
							for(Biome biome: biomeRequest.getBiomes()) {
								biomeNames.put(i, biome.getBiomeName());
								biomeIds.put(i, Biome.getIdForBiome(biome));
								i++;
							}
							
							biomeRequest = null;
							
							return new Object[] { biomeNames, biomeIds };
						}
						return new Object[] { new Object[] {}, new Object[] {} };
					default:
						if(method.isCached()) {
							cacheCommand(methodNum, arguments);
						}
				}
			}
		}
		
		return null;
	}
	
	public static String getCode(World world, BlockPos pos) {
		pos = pos.up();
		if(TreeHelper.isRooty(world.getBlockState(pos))) {
			return new JoCode().buildFromTree(world, pos).toString();
		}
		
		return "";
	}
	
	public static void setCode(World world, BlockPos pos, String treeName, String JoCode) {
		Species species = TreeRegistry.findSpeciesSloppy(treeName);
		JoCode jo = species.getJoCode(JoCode);
		if(species != Species.NULLSPECIES) {
			jo.setCareful(true).generate(world, species, pos.up(), world.getBiome(pos), EnumFacing.NORTH, 8, SafeChunkBounds.ANY);
		} else {
			Logger.getLogger(ModConstants.MODID).log(Level.WARNING, "Tree: " + treeName + " not found.");
		}
	}

	public static void createStaff(World world, BlockPos pos, String treeName, String JoCode, String rgb, boolean readOnly) {
		ItemStack stack = new ItemStack(ModItems.treeStaff, 1, 0);
		Species species = TreeRegistry.findSpeciesSloppy(treeName);
		ModItems.treeStaff.setSpecies(stack, species).setCode(stack, JoCode).setColor(stack, rgb).setReadOnly(stack, readOnly);
		EntityItem entityItem = new EntityItem(world, pos.getX() + 0.5, pos.getY() + 1.5, pos.getZ() + 0.5, stack);
		entityItem.motionX = 0;
		entityItem.motionY = 0;
		entityItem.motionZ = 0;
		world.spawnEntity(entityItem);
	}
	
	public static String getSpecies(World world, BlockPos pos) {
		IBlockState rootyState = world.getBlockState(pos.up());
		ITreePart part = TreeHelper.getTreePart(rootyState);
		if(part.isRootNode()) {
			return TreeHelper.getExactSpecies(rootyState, world, pos.up()).toString();
		}
		
		return "";
	}

	public static void plantTree(World world, BlockPos pos, String treeName) {
		Species species = TreeRegistry.findSpeciesSloppy(treeName);
		species.plantSapling(world, pos.up(2));
	}

	public static void growPulse(World world, BlockPos pos) {
		ITreePart part = TreeHelper.getTreePart(world.getBlockState(pos.up()));
		if(part.isRootNode()) {
			TreeHelper.growPulse(world, pos.up());
		}
	}
	
	public static void killTree(World world, BlockPos pos) {
		ITreePart part = TreeHelper.getTreePart(world.getBlockState(pos.up()));
		if(part.isRootNode()) {
			((BlockRooty)part).destroyTree(world, pos.up());
		}
	}

	public static int getSoilLife(World world, BlockPos pos) {
		IBlockState rootyState = world.getBlockState(pos.up());
		BlockRooty rooty = TreeHelper.getRooty(rootyState);
		if(rooty != null) {
			return rooty.getSoilLife(rootyState, world, pos.up());
		}
		return 0;
	}

	public static void setSoilLife(World world, BlockPos pos, int life) {
		ITreePart part = TreeHelper.getTreePart(world.getBlockState(pos.up()));
		if(part.isRootNode()) {
			((BlockRooty)part).setSoilLife(world, pos.up(), life);
		}
	}
	
	private class BiomeRequest {
		public BlockPos startPos;
		public BlockPos endPos;
		public int step;
		public boolean fulfilled = false;
		public ArrayList<Biome> result = new ArrayList<Biome>();

		public BiomeRequest(BlockPos start, BlockPos end, int step) {
			this.startPos = start;
			this.endPos = end;
			this.step = step;
		}
		
		//This is run by the server thread
		public synchronized void process(World world) {
			if(!fulfilled) {
				for(int z = startPos.getZ(); z < endPos.getZ(); z += step) {
					for(int x = startPos.getX(); x < endPos.getX(); x += step) {
						Biome biome = world.getBiomeProvider().getBiome(new BlockPos(x, 0, z));
						result.add(biome);
					}
				}
				fulfilled = true;
				notifyAll();
			}
		}

		//This is run by the CC thread
		public synchronized ArrayList<Biome> getBiomes() {
			while(!fulfilled) {
				try {
					wait();
				} catch (InterruptedException e) {}
			}
			return result;
		}

	}
	
	@Override
	public void attach(IComputerAccess computer) {
	}
	
	@Override
	public void detach(IComputerAccess computer) {
	}
	
	@Override
	public boolean equals(IPeripheral other) {
		return this == other;
	}
	
	/*
	public void testPoisson(World world, BlockPos pos, int rad1, int rad2, double angle, boolean onlyTight) {
		pos = pos.up();
		
		for(int y = 0; y < 2; y++) {
			for(int z = -28; z <= 28; z++) {
				for(int x = -28; x <= 28; x++) {
					world.setBlockToAir(pos.add(x, y, z));
				}
			}
		}
		
		if(rad1 >= 2 && rad2 >= 2 && rad1 <= 8 && rad2 <= 8) {
			Circle circleA = new Circle(pos, rad1);
			TreeGenerator.getTreeGenerator().makeWoolCircle(world, circleA, pos.getY(), EnumGeneratorResult.NOTREE, 3);

			Circle circleB = CircleHelper.findSecondCircle(circleA, rad2, angle, onlyTight);
			TreeGenerator.getTreeGenerator().makeWoolCircle(world, circleB, pos.getY(), EnumGeneratorResult.NOTREE, 3);
			world.setBlockState(new BlockPos(circleB.x, pos.up().getY(), circleB.z), circleB.isTight() ? Blocks.DIAMOND_BLOCK.getDefaultState() : Blocks.COBBLESTONE.getDefaultState());
		}
	}
	
	public void testPoisson2(World world, BlockPos pos, int rad1, int rad2, double angle, int rad3, boolean onlyTight) {
		pos = pos.up();
				
		//System.out.println("Test: " + "R1:" + rad1 + ", R2:" + rad2 + ", angle:" + angle + ", R3:" + rad3);
		
		for(int y = 0; y < 2; y++) {
			for(int z = -28; z <= 28; z++) {
				for(int x = -28; x <= 28; x++) {
					world.setBlockToAir(pos.add(x, y, z));
				}
			}
		}
		
		if(rad1 >= 2 && rad2 >= 2 && rad1 <= 8 && rad2 <= 8 && rad3 >= 2 && rad3 <= 8) {
			Circle circleA = new Circle(pos, rad1);
			TreeGenerator.getTreeGenerator().makeWoolCircle(world, circleA, pos.getY(), EnumGeneratorResult.NOTREE, 3);
			
			Circle circleB = CircleHelper.findSecondCircle(circleA, rad2, angle, onlyTight);
			TreeGenerator.getTreeGenerator().makeWoolCircle(world, circleB, pos.getY(), EnumGeneratorResult.NOTREE, 3);
			
			CircleHelper.maskCircles(circleA, circleB);
			
			Circle circleC = CircleHelper.findThirdCircle(circleA, circleB, rad3);
			if(circleC != null) {
				TreeGenerator.getTreeGenerator().makeWoolCircle(world, circleC, pos.getY(), EnumGeneratorResult.NOTREE, 3);
			} else {
				System.out.println("Angle:" + angle);
				world.setBlockState(new BlockPos(circleA.x, pos.up().getY(), circleA.z), Blocks.REDSTONE_BLOCK.getDefaultState());
			}
		}
	}
	
	public void testPoisson3(World world, BlockPos posA, int radA, BlockPos posB, int radB, int radC) {
		posA = posA.up();
		posB = posB.up();
				
		//System.out.println("Test: " + "R1:" + rad1 + ", R2:" + rad2 + ", angle:" + angle + ", R3:" + rad3);
		
		for(int y = 0; y < 2; y++) {
			for(int z = -28; z <= 28; z++) {
				for(int x = -28; x <= 28; x++) {
					world.setBlockToAir(posA.add(x, y, z));
				}
			}
		}
		
		if(radA >= 2 && radB >= 2 && radA <= 8 && radB <= 8 && radC >= 2 && radC <= 8) {
			Circle circleA = new Circle(posA, radA);
			TreeGenerator.getTreeGenerator().makeWoolCircle(world, circleA, posA.getY(), EnumGeneratorResult.NOTREE, 3);
			
			Circle circleB = new Circle(posB, radB);
			TreeGenerator.getTreeGenerator().makeWoolCircle(world, circleB, posB.getY(), EnumGeneratorResult.NOTREE, 3);
			
			CircleHelper.maskCircles(circleA, circleB);
			
			Circle circleC = CircleHelper.findThirdCircle(circleA, circleB, radC);
			if(circleC != null) {
				TreeGenerator.getTreeGenerator().makeWoolCircle(world, circleC, posA.getY(), EnumGeneratorResult.NOTREE, 3);
			} else {
				world.setBlockState(new BlockPos(circleA.x, posA.up().getY(), circleA.z), Blocks.REDSTONE_BLOCK.getDefaultState());
			}
		}
	}*/

	
}
