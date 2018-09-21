package com.ferreusveritas.mcf.blocks;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IRemoteActivatable {
	public boolean onRemoteActivated(World world, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumFacing facing, float hitX, float hitY, float hitZ);
}
