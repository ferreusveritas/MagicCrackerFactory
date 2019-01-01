package com.ferreusveritas.mcf.blocks;

import java.util.List;

import com.ferreusveritas.mcf.ModTabs;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockMapGuard extends Block {
	
	public BlockMapGuard() {
		super(Material.GLASS);
		setRegistryName("mapguard");
		setUnlocalizedName(getRegistryName().toString());
		setCreativeTab(ModTabs.mcfTab);
		setHardness(8);
		setResistance(8);
	}
	
	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		
		if(source instanceof World) {
			World world = (World) source;
			List<EntityItemFrame> itemFrames = world.getEntitiesWithinAABB(EntityItemFrame.class, new AxisAlignedBB(pos), null);
			if(itemFrames.isEmpty()) {
				return FULL_BLOCK_AABB;
			}
			
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
		
		return FULL_BLOCK_AABB;
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
	
}
