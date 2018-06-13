package com.ferreusveritas.mcf.event;

import java.util.Arrays;
import java.util.HashSet;

import com.ferreusveritas.mcf.util.DimBlockBounds;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
public class SecurityHandler {
	
	public static HashSet<DimBlockBounds> breakDenyBounds = new HashSet<>();
	public static HashSet<DimBlockBounds> placeDenyBounds = new HashSet<>();
	public static HashSet<DimBlockBounds> explodeDenyBounds = new HashSet<>();
	public static HashSet<DimBlockBounds> spawnDenyBounds = new HashSet<>();
	
	public static void addBreakDenyBounds( int minX, int minY, int minZ, int maxX, int maxY, int maxZ, int dim) {
		breakDenyBounds.add(new DimBlockBounds(Arrays.asList(new BlockPos(minX, minY, minZ), new BlockPos(maxX, maxY, maxZ)), dim));
	}
	
	public static void addBreakDenyBounds( DimBlockBounds bb ) {
		breakDenyBounds.add(bb);
	}

	public static void addPlaceDenyBounds( int minX, int minY, int minZ, int maxX, int maxY, int maxZ, int dim) {
		placeDenyBounds.add(new DimBlockBounds(Arrays.asList(new BlockPos(minX, minY, minZ), new BlockPos(maxX, maxY, maxZ)), dim));
	}
	
	public static void addPlaceDenyBounds( DimBlockBounds bb ) {
		placeDenyBounds.add(bb);
	}

	public static void addExplodeDenyBounds( int minX, int minY, int minZ, int maxX, int maxY, int maxZ, int dim) {
		explodeDenyBounds.add(new DimBlockBounds(Arrays.asList(new BlockPos(minX, minY, minZ), new BlockPos(maxX, maxY, maxZ)), dim));
	}
	
	public static void addExplodeDenyBounds( DimBlockBounds bb ) {
		explodeDenyBounds.add(bb);
	}

	public static void addSpawnDenyBounds( int minX, int minY, int minZ, int maxX, int maxY, int maxZ, int dim) {
		spawnDenyBounds.add(new DimBlockBounds(Arrays.asList(new BlockPos(minX, minY, minZ), new BlockPos(maxX, maxY, maxZ)), dim));
	}
	
	public static void addSpawnDenyBounds( DimBlockBounds bb ) {
		spawnDenyBounds.add(bb);
	}
	
	@SubscribeEvent
	public static void onBreakEvent(BlockEvent.BreakEvent event) {
		EntityPlayer player = event.getPlayer();
		BlockPos pos = event.getPos();
		int dim = event.getWorld().provider.getDimension();
		if(player != null && !player.isCreative() && breakDenyBounds.parallelStream().anyMatch(bb -> bb.inBounds(pos, dim)) ) {
			event.setCanceled(true);
		}
	}
	
	@SubscribeEvent
	public static void onPlaceEvent(BlockEvent.PlaceEvent event) {
		EntityPlayer player = event.getPlayer();
		BlockPos pos = event.getPos();
		int dim = event.getWorld().provider.getDimension();
		if(player != null && !player.isCreative() && placeDenyBounds.parallelStream().anyMatch(bb -> bb.inBounds(pos, dim)) ) {
			event.setCanceled(true);
		}
	}
	
	@SubscribeEvent
	public static void onExplosionEvent(ExplosionEvent.Start event) {
		int dim = event.getWorld().provider.getDimension();
		BlockPos pos = new BlockPos(event.getExplosion().getPosition());
		if(explodeDenyBounds.parallelStream().anyMatch(bb -> bb.inBounds(pos, dim))) {
			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public static void onExplosionEvent(ExplosionEvent.Detonate event) {
		int dim = event.getWorld().provider.getDimension();
		explodeDenyBounds.forEach(bb -> event.getAffectedBlocks().removeIf(p -> bb.inBounds(p, dim)));
	}
	
	@SubscribeEvent
	public static void onSpawnEvent(LivingSpawnEvent.CheckSpawn event) {
		int dim = event.getWorld().provider.getDimension();
		if(isMobHostile(event.getEntityLiving())) {
			BlockPos pos = new BlockPos(event.getX(), event.getY(), event.getZ());
			if(spawnDenyBounds.parallelStream().anyMatch(bb -> bb.inBounds(pos, dim))) {
				event.setResult(Result.DENY);
			}
		}
	}
	
	@SubscribeEvent
	public static void onEnderTeleportEvent(EnderTeleportEvent event) {
		EntityLivingBase living = event.getEntityLiving();
		int dim = living.getEntityWorld().provider.getDimension();
		BlockPos pos = new BlockPos(event.getTargetX(), event.getTargetY(), event.getTargetZ());
		if(spawnDenyBounds.parallelStream().anyMatch(bb -> bb.inBounds(pos, dim))) {
			event.setCanceled(true);
		}
	}
	
	public static boolean isMobHostile(EntityLivingBase entity) {
		return entity instanceof EntityMob || entity instanceof EntitySlime;
	}
	
}
