package com.ferreusveritas.mcf.blocks;

import java.util.List;

import com.ferreusveritas.mcf.util.Util;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockMapGuard extends Block {
	
	public BlockMapGuard() {
		super(Material.GLASS);
		setRegistryName("mapguard");
		setUnlocalizedName(getRegistryName().toString());
		setCreativeTab(Util.findCreativeTab("ComputerCraft"));
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
			
			AxisAlignedBB union = NULL_AABB;
			for(EntityItemFrame frame: itemFrames) {
				union = union == NULL_AABB ? frame.getEntityBoundingBox() : union.union(frame.getEntityBoundingBox());
			}
			
			union.offset(-pos.getX(), -pos.getY(), -pos.getZ());
			union.grow(0.03125);
			return union;
		}
		
		return FULL_BLOCK_AABB;
	}
	
	@Override
	public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
		return NULL_AABB;
	}
		
}
