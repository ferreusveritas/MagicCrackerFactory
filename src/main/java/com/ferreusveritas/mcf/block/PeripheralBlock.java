package com.ferreusveritas.mcf.block;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;

import java.util.List;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

import javax.annotation.Nullable;

public class PeripheralBlock extends Block implements EntityBlock {

    PeripheralType type;

    public PeripheralBlock(PeripheralType type) {
        super(Properties.of(Material.METAL).instabreak().strength(0.0F, 6000000.0f));
        this.type = type;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return type.newBlockEntity(pos, state);
    }

    ///////////////////////////////////////////
    // RENDERING
    ///////////////////////////////////////////

    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter level, List<Component> tooltip, TooltipFlag flag) {
        if (Screen.hasShiftDown()) {
            tooltip.add(new TranslatableComponent("peripheral.mcf.tooltip.cc_peripheral").withStyle(ChatFormatting.GOLD));
            String descriptionId = type.getDescriptionId();
            if (!descriptionId.isEmpty()) {
                tooltip.add(new TranslatableComponent(descriptionId).withStyle(ChatFormatting.GRAY));
            }
        } else {
            tooltip.add(new TranslatableComponent("peripheral.mcf.tooltip"));
        }
    }

}