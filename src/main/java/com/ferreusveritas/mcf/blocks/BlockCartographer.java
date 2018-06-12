package com.ferreusveritas.mcf.blocks;

import com.ferreusveritas.mcf.tileentity.TileCartographer;
import com.ferreusveritas.mcf.util.Util;

import dan200.computercraft.api.ComputerCraftAPI;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.peripheral.IPeripheralProvider;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class BlockCartographer extends Block implements ITileEntityProvider, IPeripheralProvider {
	
	String name = "cartographer";
	
	public BlockCartographer(Material materialIn) {
		super(materialIn);
		setRegistryName(name);
		setUnlocalizedName(name);
		setCreativeTab(Util.findCreativeTab("ComputerCraft"));
		ComputerCraftAPI.registerPeripheralProvider(this);
		GameRegistry.registerTileEntity(TileCartographer.class, name);
	}
	
	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileCartographer();
	}
	
	@Override
	public boolean hasTileEntity() {
		return true;
	}
	
	@Override
	public IPeripheral getPeripheral(World world, BlockPos pos, EnumFacing facing) {
		TileEntity te = world.getTileEntity(pos);
		
		if(te instanceof TileCartographer) {
			return (TileCartographer)te;
		}
		
		return null;
	}
	
}
