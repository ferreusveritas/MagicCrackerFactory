package com.ferreusveritas.mcf.blocks;

import java.util.List;

import com.ferreusveritas.mcf.ModConstants;
import com.ferreusveritas.mcf.ModTabs;

import dan200.computercraft.api.ComputerCraftAPI;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.peripheral.IPeripheralProvider;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockPeripheral extends Block implements ITileEntityProvider, IPeripheralProvider {
	
	PeripheralType type;
	
	public BlockPeripheral(PeripheralType type) {
		super(Material.IRON);
		this.type = type;
		setRegistryName(type.getName());
		setUnlocalizedName(type.getName());
		setCreativeTab(ModTabs.mcfTab);
		ComputerCraftAPI.registerPeripheralProvider(this);
		GameRegistry.registerTileEntity(type.getTileEntityClass(), new ResourceLocation(ModConstants.MODID, type.getName()));
		setBlockUnbreakable();
		setResistance(6000000.0f);
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
	
	///////////////////////////////////////////
	// RENDERING
	///////////////////////////////////////////

	@Override
    @SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag advanced) {
		if(GameSettings.isKeyDown(Minecraft.getMinecraft().gameSettings.keyBindSneak)) {
			tooltip.add("§6ComputerCraft Peripheral");
			String desc = type.getDesc();
			if(!desc.isEmpty()) {
				tooltip.add(desc);
			}
		} else {
			tooltip.add("Hold §E§oShift§7§r for details");
		}
	}
	
}
