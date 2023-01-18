package com.ferreusveritas.mcf.event;

import com.ferreusveritas.mcf.MCF;
import com.ferreusveritas.mcf.Registry;
import com.ferreusveritas.mcf.item.MapGuardItem;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.entity.item.ItemFrameEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
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
        if (event.getTarget() instanceof ItemFrameEntity &&
                event.getItemStack().getItem() instanceof MapGuardItem &&
                event.getWorld().getBlockState(event.getPos()).getBlock() != Registry.MAP_GUARD_BLOCK.get() &&
                !event.getPlayer().isShiftKeyDown()) {
            if (!event.getWorld().isClientSide) {
                placeBlock(event);
            }
            event.setCanceled(true);
            event.setCancellationResult(ActionResultType.SUCCESS);
        }
    }

    private static void placeBlock(PlayerInteractEvent.EntityInteractSpecific event) {
        World world = event.getWorld();
        BlockPos pos = event.getPos();
        BlockState state = ((MapGuardItem) event.getItemStack().getItem()).getPlacementState();
        world.setBlock(pos, state, 11);

        SoundType sound = state.getSoundType(world, pos, event.getPlayer());
        world.playSound(event.getPlayer(), pos, sound.getPlaceSound(), SoundCategory.BLOCKS, (sound.getVolume() + 1.0F) / 2.0F, sound.getPitch() * 0.8F);
        if (!event.getPlayer().abilities.instabuild) {
            event.getItemStack().shrink(1);
        }
    }

}
