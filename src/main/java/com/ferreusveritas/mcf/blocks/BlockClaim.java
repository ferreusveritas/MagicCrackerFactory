package com.ferreusveritas.mcf.blocks;

import com.ferreusveritas.mcf.ModConstants;
import com.ferreusveritas.mcf.ModTabs;
import com.ferreusveritas.mcf.tileentity.TileRemoteReceiver;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockClaim extends Block {

	public static final String Name = "claim";

	public BlockClaim() {
		super(Material.ROCK);
		setRegistryName(new ResourceLocation(ModConstants.MODID, Name));
		setUnlocalizedName(Name);
		setCreativeTab(ModTabs.mcfTab);
		setHardness(8.0f);
		setResistance(50.0f);
	}

	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		if(!worldIn.isRemote && placer instanceof EntityPlayer) {
			TileRemoteReceiver.broadcastClaimEvents((EntityPlayer)placer, pos, worldIn.provider.getDimension(), true);
		}
	}

	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
		if(!worldIn.isRemote) {
			TileRemoteReceiver.broadcastClaimEvents(null, pos, worldIn.provider.getDimension(), false);
		}
		super.breakBlock(worldIn, pos, state);
	}
	
	@Override
	public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {
		return 15;
	}

}
