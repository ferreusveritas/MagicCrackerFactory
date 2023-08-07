package com.ferreusveritas.mcf.event;

import com.ferreusveritas.mcf.MCF;
import com.ferreusveritas.mcf.util.ZoneManager;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.event.entity.EntityTeleportEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = MCF.MOD_ID)
public class SecurityHandler {

    @SubscribeEvent
    public static void onBreakEvent(BlockEvent.BreakEvent event) {
        if (getZoneManager(event.getWorld()).testBreakBounds(event.getPlayer(), event.getPos())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onPlaceEvent(BlockEvent.EntityPlaceEvent event) {
        if (getZoneManager(event.getWorld()).testPlaceBounds(event.getEntity(), event.getPos())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onExplosionEvent(ExplosionEvent.Start event) {
        LivingEntity entity = event.getExplosion().getSourceMob();
        if (getZoneManager(event.getWorld()).testBlastStart(new BlockPos(event.getExplosion().getPosition()), entity)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onExplosionEvent(ExplosionEvent.Detonate event) {
        LivingEntity entity = event.getExplosion().getSourceMob();
        getZoneManager(event.getWorld()).filterBlastDetonate(event.getAffectedBlocks(), entity);
    }

    @SubscribeEvent
    public static void onSpawnEvent(LivingSpawnEvent.CheckSpawn event) {
        LevelAccessor level = event.getWorld();
        if (!level.isClientSide() && getZoneManager(level).testSpawnBounds(new BlockPos(event.getX(), event.getY(), event.getZ()), event.getEntityLiving())) {
            event.setResult(Event.Result.DENY);
        }
    }

    @SubscribeEvent
    public static void onEnderTeleportEvent(EntityTeleportEvent.EnderEntity event) {
        LivingEntity player = event.getEntityLiving();
        if (getZoneManager(player.level).testEnderBounds(new BlockPos(event.getTargetX(), event.getTargetY(), event.getTargetZ()), player)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onEnderPearlTeleportEvent(EntityTeleportEvent.EnderPearl event) {
        ServerPlayer player = event.getPlayer();
        if (getZoneManager(player.level).testEnderBounds(new BlockPos(event.getTargetX(), event.getTargetY(), event.getTargetZ()), player)) {
            event.setCanceled(true);
        }
    }

    private static ZoneManager getZoneManager(LevelAccessor level) {
        return ZoneManager.get(asServerLevel(level));
    }

    private static ServerLevel asServerLevel(LevelAccessor level) {
        if (level instanceof ServerLevel serverLevel) {
            return serverLevel;
        }
        if (level instanceof WorldGenRegion worldGenRegion) {
            return worldGenRegion.getLevel();
        }
        throw new IllegalStateException("Cannot get server level for level " + level);
    }

    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load event) {
        if (!event.getWorld().isClientSide()) {
            getZoneManager(event.getWorld());
        }
    }

}
