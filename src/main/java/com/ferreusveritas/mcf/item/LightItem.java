package com.ferreusveritas.mcf.item;

import com.ferreusveritas.mcf.block.LightBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class LightItem extends BlockItem {

    private final int level;

    public LightItem(Block block, Properties properties, int level) {
        super(block, properties);
        this.level = level;
    }

    @Nullable
    @Override
    protected BlockState getPlacementState(BlockItemUseContext context) {
        return getBlock().defaultBlockState().setValue(LightBlock.LEVEL, this.level);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flag) {
        tooltip.add(new TranslationTextComponent(
                "item.mcf.light.tooltip",
                new StringTextComponent(Integer.toString(this.level))
                        .withStyle(style -> style.withColor(TextFormatting.YELLOW))
        ).withStyle(style -> style.withColor(TextFormatting.GRAY)));
    }

    @Override
    public void fillItemCategory(ItemGroup group, NonNullList<ItemStack> items) {
        if (this.allowdedIn(group) && this.level == 15) {
            items.add(new ItemStack(this));
        }
    }

}
