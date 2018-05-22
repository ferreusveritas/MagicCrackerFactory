package com.ferreusveritas.mcf.blocks;

import com.ferreusveritas.mcf.MCF;
import com.ferreusveritas.mcf.tileentity.TileCartographer;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class BlockCartographer extends Block implements ITileEntityProvider {
	
	String name = "cartographer";
	
	public BlockCartographer(Material materialIn) {
		super(materialIn);
		setRegistryName(name);
		setUnlocalizedName(name);
		setCreativeTab(MCF.mcfTab);
		GameRegistry.registerTileEntity(TileCartographer.class, "cartographer");
	}
	
	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		
		TileEntity te = world.getTileEntity(pos);
		if(!(te instanceof TileCartographer)) {
			return false;
		}
		
		TileCartographer mapManipTE = (TileCartographer) te;
		
		for(int z = 0; z < 128; z++) {
			for(int x = 0; x < 128; x++) {
				
				BlockPos top = new BlockPos(pos.getX() + x - 64, 0, pos.getZ() + z - 64);
		        Chunk chunk = world.getChunkFromBlockCoords(top);
		        int y = chunk.getTopFilledSegment() + 16;
		        top = new BlockPos(top.getX(), y, top.getZ());
		        
		        for (top = new BlockPos(top.getX(), y, top.getZ()); top.getY() >= 0; top = top.down()) {
		            IBlockState s = chunk.getBlockState(top);
		            if (s.getMaterial() != Material.AIR) {
		                break;
		            }
		        }
				
				int color = world.getBlockState(top).getMapColor(world, top).colorIndex;
				mapManipTE.setMapPixel(x, z, color, 2);
			}
		}
		
		mapManipTE.markDirty();
		
		return true;
	}
	

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileCartographer();
	}
	
	@Override
	public boolean hasTileEntity() {
		return true;
	}
	
}
