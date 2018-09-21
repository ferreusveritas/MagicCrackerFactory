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
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockRemoteButton extends BlockButton implements IRemoteActivatable {
	
    protected static final AxisAlignedBB AABB_DOWN = new AxisAlignedBB(0.3125D, 0.875D, 0.3125D, 0.6875D, 1.0D, 0.6875D);
    protected static final AxisAlignedBB AABB_UP = new AxisAlignedBB(0.3125D, 0.0D, 0.3125D, 0.6875D, 0.125D, 0.6875D);
    protected static final AxisAlignedBB AABB_NORTH = new AxisAlignedBB(0.3125D, 0.3125D, 0.875D, 0.6875D, 0.6875D, 1.0D);
    protected static final AxisAlignedBB AABB_SOUTH = new AxisAlignedBB(0.3125D, 0.3125D, 0.0D, 0.6875D, 0.6875D, 0.125D);
    protected static final AxisAlignedBB AABB_WEST = new AxisAlignedBB(0.875D, 0.3125D, 0.3125D, 1.0D, 0.6875D, 0.6875D);
    protected static final AxisAlignedBB AABB_EAST = new AxisAlignedBB(0.0D, 0.3125D, 0.3125D, 0.125D, 0.6875D, 0.6875D);
	
	public BlockRemoteButton() {
		super(false);
		setRegistryName("remotebutton");
		setUnlocalizedName(getRegistryName().toString());
		this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(BlockButton.POWERED, Boolean.valueOf(false)));
		this.setTickRandomly(true);
		setCreativeTab(Util.findCreativeTab("ComputerCraft"));
	}
	
	@Override
	public boolean onRemoteActivated(World world, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumFacing facing, float hitX, float hitY, float hitZ) {
		return super.onBlockActivated(world, pos, state, playerIn, EnumHand.MAIN_HAND, facing, hitX, hitY, hitZ);
	}
	
	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {		
		return false;
	}
	
	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		EnumFacing enumfacing = state.getValue(FACING);
		
		switch (enumfacing) {
			case DOWN: return AABB_DOWN;
			default:
			case UP: return AABB_UP;
			case NORTH: return AABB_NORTH;
			case SOUTH: return AABB_SOUTH;
			case WEST: return AABB_WEST;
			case EAST: return AABB_EAST;
		}
	}
	
	@Override
	public int tickRate(World worldIn) {
		return 10;
	}
	
	@Override
	protected void playClickSound(@Nullable EntityPlayer player, World worldIn, BlockPos pos) {
		worldIn.playSound(player, pos, SoundEvents.BLOCK_STONE_BUTTON_CLICK_ON, SoundCategory.BLOCKS, 0.3F, 0.6F);
	}
	
	@Override
	protected void playReleaseSound(World worldIn, BlockPos pos) {
		worldIn.playSound((EntityPlayer)null, pos, SoundEvents.BLOCK_STONE_BUTTON_CLICK_OFF, SoundCategory.BLOCKS, 0.3F, 0.5F);
	}
	
}