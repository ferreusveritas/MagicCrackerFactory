package com.ferreusveritas.mcf.item;

import com.ferreusveritas.mcf.block.MapGuardBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

import javax.annotation.Nullable;

public class MapGuardItem extends BlockItem {

    private final boolean lit;

    public MapGuardItem(Block block, Properties properties, boolean lit) {
        super(block, properties);
        this.lit = lit;
    }

    public BlockState getPlacementState() {
        return getBlock().defaultBlockState().setValue(MapGuardBlock.LIT, this.lit);
    }

    @Nullable
    @Override
    protected BlockState getPlacementState(BlockItemUseContext context) {
        return getPlacementState();
    }

    @Override
    public String getDescriptionId() {
        return getOrCreateDescriptionId();
    }

    @Override
    public void fillItemCategory(ItemGroup group, NonNullList<ItemStack> items) {
        if (this.allowdedIn(group)) {
            items.add(new ItemStack(this));
        }
    }

}
