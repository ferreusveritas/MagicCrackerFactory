package com.ferreusveritas.mcf.blocks;

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

public class BlockPeripheral extends Block implements ITileEntityProvider, IPeripheralProvider {
	
	PeripheralType type;
	
	public BlockPeripheral(PeripheralType type) {
		super(Material.IRON);
		this.type = type;
		setRegistryName(type.getName());
		setUnlocalizedName(type.getName());
		setCreativeTab(Util.findCreativeTab("ComputerCraft"));
		ComputerCraftAPI.registerPeripheralProvider(this);
		GameRegistry.registerTileEntity(type.getTileEntityClass(), type.getName());
	}
	
	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return type.newTileEntity();
	}
	
	@Override
	public boolean hasTileEntity() {
		return true;
	}
	
	@Override
	public IPeripheral getPeripheral(World world, BlockPos pos, EnumFacing facing) {
		TileEntity te = world.getTileEntity(pos);
		
		if(type.getTileEntityClass().isInstance(te)) {
			return (IPeripheral) te;
		}
		
		return null;
	}
	
}
