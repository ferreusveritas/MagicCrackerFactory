package com.ferreusveritas.mcf.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ZoneManager {
	public static HashMap<Integer, DimBoundsStorage> dimBoundsStorage = new HashMap<>();
	
	public static DimBoundsStorage getDimBoundsStorage(int dim) {
		return dimBoundsStorage.computeIfAbsent(dim, key -> { return new DimBoundsStorage(key); } );
	}
	
	//Additions
	
	public static void addBreakDenyBounds(String name, IBounds bb, int dim) {
		getDimBoundsStorage(dim).breakDenyBounds.put(name, bb);
	}
	
	public static void addPlaceDenyBounds(String name, IBounds bb, int dim ) {
		getDimBoundsStorage(dim).placeDenyBounds.put(name, bb);
	}
	
	public static void addExplodeDenyBounds(String name, IBounds bb, int dim ) {
		getDimBoundsStorage(dim).explodeDenyBounds.put(name, bb);
	}
	
	public static void addSpawnDenyBounds(String name, BlockBounds bb, int dim ) {
		getDimBoundsStorage(dim).spawnDenyBounds.put(name, bb);
	}
	
	//Additions
	
	public static void addBreakDenyBounds(String name, int minX, int minY, int minZ, int maxX, int maxY, int maxZ, int dim) {
		addBreakDenyBounds(name, new BlockBounds(Arrays.asList(new BlockPos(minX, minY, minZ), new BlockPos(maxX, maxY, maxZ))), dim);
	}
	
	public static void addPlaceDenyBounds(String name, int minX, int minY, int minZ, int maxX, int maxY, int maxZ, int dim) {
		addPlaceDenyBounds(name, new BlockBounds(Arrays.asList(new BlockPos(minX, minY, minZ), new BlockPos(maxX, maxY, maxZ))), dim);
	}
	
	public static void addExplodeDenyBounds(String name, int minX, int minY, int minZ, int maxX, int maxY, int maxZ, int dim) {
		addExplodeDenyBounds(name, new BlockBounds(Arrays.asList(new BlockPos(minX, minY, minZ), new BlockPos(maxX, maxY, maxZ))), dim);
	}
	
	public static void addSpawnDenyBounds(String name, int minX, int minY, int minZ, int maxX, int maxY, int maxZ, int dim) {
		addSpawnDenyBounds(name, new BlockBounds(Arrays.asList(new BlockPos(minX, minY, minZ), new BlockPos(maxX, maxY, maxZ))), dim);
	}
	
	//Removals
	
	public static void remBreakDenyBounds(String name, int dim) {
		getDimBoundsStorage(dim).breakDenyBounds.remove(name);
	}
	
	public static void remPlaceDenyBounds(String name, int dim) {
		getDimBoundsStorage(dim).placeDenyBounds.remove(name);
	}
	
	public static void remExplodeDenyBounds(String name, int dim) {
		getDimBoundsStorage(dim).explodeDenyBounds.remove(name);
	}
	
	public static void remSpawnDenyBounds(String name, int dim) {
		getDimBoundsStorage(dim).spawnDenyBounds.remove(name);
	}
	
	//Tests
	
	public static boolean testBreakBounds(EntityPlayer player, BlockPos pos, int dim) {
		return player != null && !player.isCreative() && getDimBoundsStorage(dim).breakDenyBounds.values().parallelStream().anyMatch(bb -> bb.inBounds(pos));
	}
	
	public static boolean testPlaceBounds(EntityPlayer player, BlockPos pos, int dim) {
		return player != null && !player.isCreative() && getDimBoundsStorage(dim).placeDenyBounds.values().parallelStream().anyMatch(bb -> bb.inBounds(pos));
	}
	
	public static boolean testExplosionStart(BlockPos pos, int dim) {
		return getDimBoundsStorage(dim).explodeDenyBounds.values().parallelStream().anyMatch(bb -> bb.inBounds(pos));
	}
	
	public static void filterExplosionDetonate(List<BlockPos> blocks, int dim) {
		getDimBoundsStorage(dim).explodeDenyBounds.values().forEach(bb -> blocks.removeIf(p -> bb.inBounds(p)));
	}
	
	public static boolean testSpawnBounds(BlockPos pos, int dim) {
		return getDimBoundsStorage(dim).spawnDenyBounds.values().parallelStream().anyMatch(bb -> bb.inBounds(pos));
	}
	
	//IO
	
	public static boolean loadFromSave() {
		return false;
	}
	
	public static boolean saveToDisk() {
		return false;
	}
	
	public static String getSaveFile(World world) {
		//String s = Minecraft.getMinecraftDir().getCanonicalPath() + "/saves/" + world.getSaveHandler().getSaveDirectoryName() + "/data/AA/World" + world.provider.dimensionId + ".dat";
		return "";
	}
	
}
