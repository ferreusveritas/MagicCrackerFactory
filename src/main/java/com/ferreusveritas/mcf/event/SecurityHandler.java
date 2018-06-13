package com.ferreusveritas.mcf.event;

import com.ferreusveritas.mcf.util.ZoneManager;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
public class SecurityHandler {
	
	@SubscribeEvent
	public static void onBreakEvent(BlockEvent.BreakEvent event) {
		if( ZoneManager.testBreakBounds(event.getPlayer(), event.getPos(), event.getWorld().provider.getDimension()) ) {
			event.setCanceled(true);
		}
	}
	
	@SubscribeEvent
	public static void onPlaceEvent(BlockEvent.PlaceEvent event) {
		if( ZoneManager.testPlaceBounds(event.getPlayer(), event.getPos(), event.getWorld().provider.getDimension()) ) {
			event.setCanceled(true);
		}
	}
	
	@SubscribeEvent
	public static void onExplosionEvent(ExplosionEvent.Start event) {
		if(ZoneManager.testExplosionStart(new BlockPos(event.getExplosion().getPosition()), event.getWorld().provider.getDimension())) {
			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public static void onExplosionEvent(ExplosionEvent.Detonate event) {
		ZoneManager.filterExplosionDetonate(event.getAffectedBlocks(), event.getWorld().provider.getDimension());
	}
	
	@SubscribeEvent
	public static void onSpawnEvent(LivingSpawnEvent.CheckSpawn event) {
		if(isMobHostile(event.getEntityLiving())) {
			if(ZoneManager.testSpawnBounds(new BlockPos(event.getX(), event.getY(), event.getZ()), event.getWorld().provider.getDimension())) {
				event.setResult(Result.DENY);
			}
		}
	}
	
	@SubscribeEvent
	public static void onEnderTeleportEvent(EnderTeleportEvent event) {
		EntityLivingBase living = event.getEntityLiving();
		if(ZoneManager.testSpawnBounds(new BlockPos(event.getTargetX(), event.getTargetY(), event.getTargetZ()), living.getEntityWorld().provider.getDimension())) {
			event.setCanceled(true);
		}
	}
	
	public static boolean isMobHostile(EntityLivingBase entity) {
		return entity instanceof EntityMob || entity instanceof EntitySlime;
	}
	
}
