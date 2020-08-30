package com.ferreusveritas.mcf.event;

import com.ferreusveritas.mcf.util.ZoneManager;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
public class SecurityHandler {
	
	@SubscribeEvent
	public void onWorldLoad(WorldEvent.Load event) {
		ZoneManager.get(event.getWorld());
	}
	
	@SubscribeEvent
	public static void onBreakEvent(BlockEvent.BreakEvent event) {
		if( ZoneManager.get(event.getWorld()).testBreakBounds(event.getPlayer(), event.getPos()) ) {
			event.setCanceled(true);
		}
	}
	
	@SubscribeEvent
	public static void onPlaceEvent(BlockEvent.PlaceEvent event) {
		if( ZoneManager.get(event.getWorld()).testPlaceBounds(event.getPlayer(), event.getPos()) ) {
			event.setCanceled(true);
		}
	}
	
	@SubscribeEvent
	public static void onExplosionEvent(ExplosionEvent.Start event) {
		EntityLivingBase living = event.getExplosion().getExplosivePlacedBy();
		if(ZoneManager.get(event.getWorld()).testBlastStart(new BlockPos(event.getExplosion().getPosition()), living) ) {
			event.setCanceled(true);
		}
	}
	
	@SubscribeEvent
	public static void onExplosionEvent(ExplosionEvent.Detonate event) {
		EntityLivingBase living = event.getExplosion().getExplosivePlacedBy();
		ZoneManager.get(event.getWorld()).filterBlastDetonate(event.getAffectedBlocks(), living);
	}
	
	@SubscribeEvent
	public static void onSpawnEvent(LivingSpawnEvent.CheckSpawn event) {
		if(ZoneManager.get(event.getWorld()).testSpawnBounds(new BlockPos(event.getX(), event.getY(), event.getZ()), event.getEntityLiving())) {
			event.setResult(Result.DENY);
		}
	}
	
	@SubscribeEvent
	public static void onEnderTeleportEvent(EnderTeleportEvent event) {
		EntityLivingBase living = event.getEntityLiving();
		if(ZoneManager.get(living.world).testEnderBounds(new BlockPos(event.getTargetX(), event.getTargetY(), event.getTargetZ()), living)) {
			event.setCanceled(true);
		}
	}
	
	/*
	@SubscribeEvent
	public static void SeedVoluntaryPlantEvent(SeedVoluntaryPlantEvent event) {
		if(ZoneManager.get(event.getEntityItem().world).testSeedsBounds(event.getPos())) {
			event.setCanceled(true);
		}
	}
	
	@SubscribeEvent
	public static void SeedVoluntaryDropEvent(SeedVoluntaryDropEvent event) {
		if(ZoneManager.get(event.getWorld()).testSeedsBounds(event.getRootPos())) {
			event.setCanceled(true);
		}
	}*/
	
}
