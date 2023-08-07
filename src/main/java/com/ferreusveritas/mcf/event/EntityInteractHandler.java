package com.ferreusveritas.mcf.event;

import com.ferreusveritas.mcf.MCF;
import com.ferreusveritas.mcf.Registry;
import com.ferreusveritas.mcf.item.MapGuardItem;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = MCF.MOD_ID)
public class EntityInteractHandler {

    /**
     * Allows placing map guard by right-clicking map directly. As a way around this in case the player wants to place the map
     * guard in the item frame, they can hold shift to mitigate this behaviour.
     */
    @SubscribeEvent
    public static void onEntityInteract(PlayerInteractEvent.EntityInteractSpecific event) {
        if (event.getTarget() instanceof ItemFrame &&
                event.getItemStack().getItem() instanceof MapGuardItem &&
                event.getWorld().getBlockState(event.getPos()).getBlock() != Registry.MAP_GUARD_BLOCK.get() &&
                !event.getPlayer().isShiftKeyDown()) {
            if (!event.getWorld().isClientSide) {
                placeBlock(event);
            }
            event.setCanceled(true);
            event.setCancellationResult(InteractionResult.SUCCESS);
        }
    }

    private static void placeBlock(PlayerInteractEvent.EntityInteractSpecific event) {
        Level level = event.getWorld();
        BlockPos pos = event.getPos();
        BlockState state = ((MapGuardItem) event.getItemStack().getItem()).getPlacementState();
        level.setBlock(pos, state, 11);

        SoundType sound = state.getSoundType(level, pos, event.getPlayer());
        level.playSound(event.getPlayer(), pos, sound.getPlaceSound(), SoundSource.BLOCKS, (sound.getVolume() + 1.0F) / 2.0F, sound.getPitch() * 0.8F);
        if (!event.getPlayer().getAbilities().instabuild) {
            event.getItemStack().shrink(1);
        }
    }

}
