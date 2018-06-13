package com.ferreusveritas.mcf.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;

public class ZoneManager {
	public static HashMap<String, IBounds> breakDenyBounds = new HashMap<>();
	public static HashMap<String, IBounds> placeDenyBounds = new HashMap<>();
	public static HashMap<String, IBounds> explodeDenyBounds = new HashMap<>();
	public static HashMap<String, IBounds> spawnDenyBounds = new HashMap<>();
	
	public static void addBreakDenyBounds(String name, int minX, int minY, int minZ, int maxX, int maxY, int maxZ, int dim) {
		breakDenyBounds.put(name, new DimBlockBounds(Arrays.asList(new BlockPos(minX, minY, minZ), new BlockPos(maxX, maxY, maxZ)), dim));
	}
	
	public static void addBreakDenyBounds(String name, IBounds bb ) {
		breakDenyBounds.put(name, bb);
	}

	public static void addPlaceDenyBounds(String name, int minX, int minY, int minZ, int maxX, int maxY, int maxZ, int dim) {
		placeDenyBounds.put(name, new DimBlockBounds(Arrays.asList(new BlockPos(minX, minY, minZ), new BlockPos(maxX, maxY, maxZ)), dim));
	}
	
	public static void addPlaceDenyBounds(String name, IBounds bb ) {
		placeDenyBounds.put(name, bb);
	}

	public static void addExplodeDenyBounds(String name, int minX, int minY, int minZ, int maxX, int maxY, int maxZ, int dim) {
		explodeDenyBounds.put(name, new DimBlockBounds(Arrays.asList(new BlockPos(minX, minY, minZ), new BlockPos(maxX, maxY, maxZ)), dim));
	}
	
	public static void addExplodeDenyBounds(String name, IBounds bb ) {
		explodeDenyBounds.put(name, bb);
	}

	public static void addSpawnDenyBounds(String name, int minX, int minY, int minZ, int maxX, int maxY, int maxZ, int dim) {
		spawnDenyBounds.put(name, new DimBlockBounds(Arrays.asList(new BlockPos(minX, minY, minZ), new BlockPos(maxX, maxY, maxZ)), dim));
	}
	
	public static void addSpawnDenyBounds(String name, DimBlockBounds bb ) {
		spawnDenyBounds.put(name, bb);
	}
	
	public static boolean testBreakBounds(EntityPlayer player, BlockPos pos, int dim) {
		return player != null && !player.isCreative() && breakDenyBounds.values().parallelStream().anyMatch(bb -> bb.inBounds(pos, dim));
	}
	
	public static boolean testPlaceBounds(EntityPlayer player, BlockPos pos, int dim) {
		return player != null && !player.isCreative() && placeDenyBounds.values().parallelStream().anyMatch(bb -> bb.inBounds(pos, dim));
	}
	
	public static boolean testExplosionStart(BlockPos pos, int dim) {
		return ZoneManager.explodeDenyBounds.values().parallelStream().anyMatch(bb -> bb.inBounds(pos, dim));
	}
	
	public static void filterExplosionDetonate(List<BlockPos> blocks, int dim) {
		ZoneManager.explodeDenyBounds.values().forEach(bb -> blocks.removeIf(p -> bb.inBounds(p, dim)));
	}
	
	public static boolean testSpawnBounds(BlockPos pos, int dim) {
		return ZoneManager.spawnDenyBounds.values().parallelStream().anyMatch(bb -> bb.inBounds(pos, dim));
	}
}
