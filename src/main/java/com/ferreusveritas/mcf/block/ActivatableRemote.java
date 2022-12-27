package com.ferreusveritas.mcf.block;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;

public interface ActivatableRemote {
    ActionResultType activate(World world, BlockPos pos, BlockState state, PlayerEntity player, BlockRayTraceResult result);
}
