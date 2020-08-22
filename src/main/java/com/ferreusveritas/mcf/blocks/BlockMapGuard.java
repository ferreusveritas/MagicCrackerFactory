package com.ferreusveritas.mcf.blocks;

import java.util.List;

import com.ferreusveritas.mcf.MCF;
import com.ferreusveritas.mcf.ModTabs;
import com.ferreusveritas.mcf.entities.EntityItemDisplay;
import com.ferreusveritas.mcf.features.Remote;
import com.ferreusveritas.mcf.network.PacketTouchMap;

import net.minecraft.block.Block;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockMapGuard extends Block {

	protected static final PropertyBool LIT = PropertyBool.create("lit");

	public BlockMapGuard() {
		super(Material.GLASS);
		setRegistryName("mapguard");
		setUnlocalizedName(getRegistryName().toString());
		setCreativeTab(ModTabs.mcfTab);
		setDefaultState(getDefaultState().withProperty(LIT, false));
		setHardness(8);
		setResistance(8);
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		
		if(source instanceof World) {
			World world = (World) source;
			List<EntityItemFrame> itemFrames = world.getEntitiesWithinAABB(EntityItemFrame.class, new AxisAlignedBB(pos), null);
			if(!itemFrames.isEmpty()) {
				BlockPos offPos = new BlockPos(-pos.getX(), -pos.getY(), -pos.getZ());
				
				AxisAlignedBB union = NULL_AABB;
				for(EntityItemFrame frame: itemFrames) {
					
					ItemStack displayedItem = frame.getDisplayedItem();
					if(!displayedItem.isEmpty() && displayedItem.getItem() instanceof net.minecraft.item.ItemMap) {
						union = union == NULL_AABB ? frame.getEntityBoundingBox().offset(offPos) : union.union(frame.getEntityBoundingBox().offset(offPos));
						union = new AxisAlignedBB(
								union.minX == 0.125 ? 0 : union.minX,
								union.minY == 0.125 ? 0 : union.minY,
								union.minZ == 0.125 ? 0 : union.minZ,
								union.maxX == 0.875 ? 1 : union.maxX,
								union.maxY == 0.875 ? 1 : union.maxY,
								union.maxZ == 0.875 ? 1 : union.maxZ
							);
					} else {
						union = union == NULL_AABB ? frame.getEntityBoundingBox().offset(offPos) : union.union(frame.getEntityBoundingBox().offset(offPos));	
					}
					
				}
				
				return union;
			}
			
			List<EntityItemDisplay> itemDisplays = world.getEntitiesWithinAABB(EntityItemDisplay.class, new AxisAlignedBB(pos), null);
			if(!itemDisplays.isEmpty()) {
				BlockPos offPos = new BlockPos(-pos.getX(), -pos.getY(), -pos.getZ());
				
				AxisAlignedBB union = NULL_AABB;
				for(EntityItemDisplay display: itemDisplays) {
					union = union == NULL_AABB ? display.getEntityBoundingBox().offset(offPos) : union.union(display.getEntityBoundingBox().offset(offPos));
				}
				
				return union;
			}
			
		}

		return FULL_BLOCK_AABB;
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		ItemStack heldItem = playerIn.getHeldItem(hand);

		if(heldItem.getItem() != Remote.universalRemote) {
			if(worldIn.isRemote) {
				MCF.network.sendToServer(new PacketTouchMap(new Vec3d(hitY, hitY, hitZ), pos, facing));
			}
			return true;
		}

		return false;
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] { LIT } );
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(LIT) ? 1 : 0;
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(LIT, meta == 1);
	}

	@Override
	public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
		return getDefaultState().withProperty(LIT, meta == 1);
	}

	@Override
	public int damageDropped(IBlockState state) {
		return state.getValue(LIT) ? 1 : 0;
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
		return NULL_AABB;
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.INVISIBLE;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isReplaceable(IBlockAccess worldIn, BlockPos pos) {
		return true;
	}

	@Override
	public int getLightValue(IBlockState state) {
		return state.getValue(LIT).booleanValue() ? 15 : 0; 
	}

	@Override
	public EnumPushReaction getMobilityFlag(IBlockState state) {
		return EnumPushReaction.BLOCK;
	}

	@Override
	public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
		return BlockFaceShape.UNDEFINED;
	}

	@Override
	public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {
		items.add(new ItemStack(this, 1, 0));
		items.add(new ItemStack(this, 1, 1));
	}

}
