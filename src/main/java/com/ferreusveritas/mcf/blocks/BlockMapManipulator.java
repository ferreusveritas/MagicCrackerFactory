package com.ferreusveritas.mcf.blocks;

import com.ferreusveritas.mcf.MCF;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapData;

public class BlockMapManipulator extends Block {
	
	String name = "mapmanipulator";
	
	public BlockMapManipulator(Material materialIn) {
		super(materialIn);
		setRegistryName(name);
		setUnlocalizedName(name);
		setCreativeTab(MCF.mcfTab);
	}
	
	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		changeMap(worldIn);
		return true;
	}
	
	public void changeMap(World world) {
		MapData mapData = getMapData(world, 0);

		int p = 0;
		for(int z = 0; z < 128; z++) {
			for(int x = 0; x < 128; x++) {
				setPixel(mapData, x, z, p++ % ((51 * 4) + 1));//world.rand.nextInt(52));
			}
		}
		
		mapData.markDirty();
	}
	
	MapData getMapData(World world, int mapNum) {
		return (MapData) world.loadData(MapData.class, "map_" + mapNum);
	}
	
	public void setPixel(MapData mapData, int x, int z, int color52, int index4) {
		setPixel(mapData, index4, z, color52 * 4 | index4);
	}
	
	public void setPixel(MapData mapData, int x, int z, int colorFull) {

		if(colorFull > (51 * 4) + 1) {
			colorFull = 0;
		}
		
		if(x >= 0 && x < 128 && z >= 0 && z < 128) {
			mapData.colors[x + z * 128] = (byte) colorFull;
		}
	}
	
}
