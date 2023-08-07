package com.ferreusveritas.mcf.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public interface ActivatableRemote {
    InteractionResult activate(Level level, BlockPos pos, BlockState state, Player player, BlockHitResult hit);
}
