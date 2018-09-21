package com.ferreusveritas.mcf.blocks;

import javax.annotation.Nullable;

import com.ferreusveritas.mcf.util.Util;

import net.minecraft.block.BlockButton;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockRemoteButton extends BlockButton implements IRemoteActivatable {
	
	public BlockRemoteButton() {
		super(false);
		setRegistryName("remotebutton");
		setUnlocalizedName(getRegistryName().toString());
		this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(BlockButton.POWERED, Boolean.valueOf(false)));
		this.setTickRandomly(true);
		setCreativeTab(Util.findCreativeTab("ComputerCraft"));
	}
	
	public boolean onRemoteActivated(World world, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumFacing facing, float hitX, float hitY, float hitZ) {
		return super.onBlockActivated(world, pos, state, playerIn, EnumHand.MAIN_HAND, facing, hitX, hitY, hitZ);
	}
	
	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {		
		return true;
	}
	
	@Override
	public int tickRate(World worldIn) {
		return 10;
	}
	
	protected void playClickSound(@Nullable EntityPlayer player, World worldIn, BlockPos pos) {
		worldIn.playSound(player, pos, SoundEvents.BLOCK_STONE_BUTTON_CLICK_ON, SoundCategory.BLOCKS, 0.3F, 0.6F);
	}
	
	protected void playReleaseSound(World worldIn, BlockPos pos) {
		worldIn.playSound((EntityPlayer)null, pos, SoundEvents.BLOCK_STONE_BUTTON_CLICK_OFF, SoundCategory.BLOCKS, 0.3F, 0.5F);
	}
	
}