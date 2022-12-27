package com.ferreusveritas.mcf.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

public class PeripheralBlock extends Block {

    PeripheralType type;

    public PeripheralBlock(PeripheralType type) {
        super(Properties.of(Material.METAL).instabreak().strength(0.0F, 6000000.0f));
        this.type = type;
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return type.createTileEntity();
    }

    ///////////////////////////////////////////
    // RENDERING
    ///////////////////////////////////////////

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, @Nullable IBlockReader level, List<ITextComponent> tooltip, ITooltipFlag flag) {
        if (Screen.hasShiftDown()) {
            tooltip.add(new TranslationTextComponent("peripheral.mcf.tooltip.cc_peripheral").withStyle(TextFormatting.GOLD));
            String descriptionId = type.getDescriptionId();
            if (!descriptionId.isEmpty()) {
                tooltip.add(new TranslationTextComponent(descriptionId).withStyle(TextFormatting.GRAY));
            }
        } else {
            tooltip.add(new TranslationTextComponent("peripheral.mcf.tooltip"));
        }
    }

}