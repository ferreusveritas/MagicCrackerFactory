package com.ferreusveritas.mcf.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
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
	}
	
	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		changeMap(worldIn);
		return true;
	}
	
	public void changeMap(World world) {

		ItemStack map = new ItemStack(Items.FILLED_MAP, 1, 0);
		
        String s = "map_" + map.getMetadata();
        MapData mapdata = new MapData(s);
        
        for(int i = 0; i < (128*128); i++) {
        	mapdata.colors[i] = 7;
        }
        
        mapdata.setDirty(true);
        
        world.setData(s, mapdata);
		
	}
	
}
