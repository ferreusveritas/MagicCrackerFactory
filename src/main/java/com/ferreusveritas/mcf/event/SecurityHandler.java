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
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
public class SecurityHandler {

	@SubscribeEvent
	public static void onWorldLoad(WorldEvent.Load event) {
		if(!event.getWorld().isRemote) {
			System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX TEST " + event.getPhase() + " Dim: " + event.getWorld().provider.getDimension());
			ZoneManager.forWorld(event.getWorld());
		}
	}
	
	@SubscribeEvent
	public static void onWorldSave(WorldEvent.Save event) {
		
	}
	
	@SubscribeEvent
	public static void onBreakEvent(BlockEvent.BreakEvent event) {
		if( ZoneManager.getZoneManager(event.getWorld()).testBreakBounds(event.getPlayer(), event.getPos()) ) {
			event.setCanceled(true);
		}
	}
	
	@SubscribeEvent
	public static void onPlaceEvent(BlockEvent.PlaceEvent event) {
		if( ZoneManager.getZoneManager(event.getWorld()).testPlaceBounds(event.getPlayer(), event.getPos()) ) {
			event.setCanceled(true);
		}
	}
	
	@SubscribeEvent
	public static void onExplosionEvent(ExplosionEvent.Start event) {
		if(ZoneManager.getZoneManager(event.getWorld()).testBlastStart(new BlockPos(event.getExplosion().getPosition())) ) {
			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public static void onExplosionEvent(ExplosionEvent.Detonate event) {
		ZoneManager.getZoneManager(event.getWorld()).filterBlastDetonate(event.getAffectedBlocks() );
	}
	
	@SubscribeEvent
	public static void onSpawnEvent(LivingSpawnEvent.CheckSpawn event) {
		if(isMobHostile(event.getEntityLiving())) {
			if(ZoneManager.getZoneManager(event.getWorld()).testSpawnBounds(new BlockPos(event.getX(), event.getY(), event.getZ())) ) {
				event.setResult(Result.DENY);
			}
		}
	}
	
	@SubscribeEvent
	public static void onEnderTeleportEvent(EnderTeleportEvent event) {
		EntityLivingBase living = event.getEntityLiving();
		if(ZoneManager.getZoneManager(living.world).testSpawnBounds(new BlockPos(event.getTargetX(), event.getTargetY(), event.getTargetZ()))) {
			event.setCanceled(true);
		}
	}
	
	public static boolean isMobHostile(EntityLivingBase entity) {
		return entity instanceof EntityMob || entity instanceof EntitySlime;
	}
	
}
