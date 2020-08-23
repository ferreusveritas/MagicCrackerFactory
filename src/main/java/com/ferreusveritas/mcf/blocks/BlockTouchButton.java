package com.ferreusveritas.mcf.blocks;

import javax.annotation.Nullable;

import com.ferreusveritas.mcf.MCF;
import com.ferreusveritas.mcf.ModTabs;
import com.ferreusveritas.mcf.network.PacketTouchMap;

import net.minecraft.block.BlockButton;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockTouchButton extends BlockButton {

	protected static final AxisAlignedBB AABB_DOWN_OFF 	= new AxisAlignedBB(0.3125D, 0.875D,  0.3125D,  0.6875D, 1.0D,    0.6875D);
	protected static final AxisAlignedBB AABB_UP_OFF 	= new AxisAlignedBB(0.3125D, 0.0D,    0.3125D,  0.6875D, 0.125D,  0.6875D);
	protected static final AxisAlignedBB AABB_NORTH_OFF = new AxisAlignedBB(0.3125D, 0.3125D, 0.875D,   0.6875D, 0.6875D, 1.0D);
	protected static final AxisAlignedBB AABB_SOUTH_OFF = new AxisAlignedBB(0.3125D, 0.3125D, 0.0D,     0.6875D, 0.6875D, 0.125D);
	protected static final AxisAlignedBB AABB_WEST_OFF 	= new AxisAlignedBB(0.875D,  0.3125D, 0.3125D,  1.0D,    0.6875D, 0.6875D);
	protected static final AxisAlignedBB AABB_EAST_OFF 	= new AxisAlignedBB(0.0D,    0.3125D, 0.3125D,  0.125D,  0.6875D, 0.6875D);

	protected static final AxisAlignedBB AABB_DOWN_ON 	= new AxisAlignedBB(0.3125D,  0.93125D, 0.3125D,  0.6875D,  1.0D,     0.6875D);
	protected static final AxisAlignedBB AABB_UP_ON 	= new AxisAlignedBB(0.3125D,  0.0D,     0.3125D,  0.6875D,  0.06875D, 0.6875D);
	protected static final AxisAlignedBB AABB_NORTH_ON 	= new AxisAlignedBB(0.3125D,  0.3125D,  0.93125D, 0.6875D,  0.6875D,  1.0D);
	protected static final AxisAlignedBB AABB_SOUTH_ON 	= new AxisAlignedBB(0.3125D,  0.3125D,  0.0D,     0.6875D,  0.6875D,  0.06875D);
	protected static final AxisAlignedBB AABB_WEST_ON 	= new AxisAlignedBB(0.93125D, 0.3125D,  0.3125D,  1.0D,     0.6875D,  0.6875D);
	protected static final AxisAlignedBB AABB_EAST_ON 	= new AxisAlignedBB(0.0D,     0.3125D,  0.3125D,  0.06875D, 0.6875D,  0.6875D);

	public BlockTouchButton() {
		super(false);
		setRegistryName("touchbutton");
		setUnlocalizedName(getRegistryName().toString());
		setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(BlockButton.POWERED, Boolean.valueOf(false)));
		setTickRandomly(true);
		setHardness(3.0f);
		setResistance(10.0f);
		setCreativeTab(ModTabs.mcfTab);
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (!((Boolean)state.getValue(POWERED)).booleanValue()) {
			if(worldIn.isRemote) {
				MCF.network.sendToServer(new PacketTouchMap(new Vec3d(hitY, hitY, hitZ), pos, facing));
			} 
			return true;
		}

		return false;
	}

	public boolean buttonActivate(World worldIn, BlockPos pos, IBlockState state) {
		if (((Boolean)state.getValue(POWERED)).booleanValue()) {
			return true;
		}
		else {
			EnumFacing facing = state.getValue(FACING);
			worldIn.setBlockState(pos, state.withProperty(POWERED, Boolean.valueOf(true)), 3);
			worldIn.markBlockRangeForRenderUpdate(pos, pos);
			this.playClickSound(null, worldIn, pos);
			worldIn.notifyNeighborsOfStateChange(pos, this, false);
			worldIn.notifyNeighborsOfStateChange(pos.offset(facing.getOpposite()), this, false);
			worldIn.scheduleUpdate(pos, this, this.tickRate(worldIn));
			return true;
		}
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		EnumFacing enumfacing = (EnumFacing)state.getValue(FACING);
		boolean flag = ((Boolean)state.getValue(POWERED)).booleanValue();

		switch (enumfacing) {
			case EAST: return flag ? AABB_EAST_ON : AABB_EAST_OFF;
			case WEST: return flag ? AABB_WEST_ON : AABB_WEST_OFF;
			case SOUTH: return flag ? AABB_SOUTH_ON : AABB_SOUTH_OFF;
			case NORTH:
			default: return flag ? AABB_NORTH_ON : AABB_NORTH_OFF;
			case UP: return flag ? AABB_UP_ON : AABB_UP_OFF;
			case DOWN: return flag ? AABB_DOWN_ON : AABB_DOWN_OFF;
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