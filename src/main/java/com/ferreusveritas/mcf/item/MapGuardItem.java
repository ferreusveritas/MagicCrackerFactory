package com.ferreusveritas.mcf.item;

import com.ferreusveritas.mcf.block.MapGuardBlock;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

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
    protected BlockState getPlacementState(BlockPlaceContext context) {
        return getPlacementState();
    }

    @Override
    public String getDescriptionId() {
        return getOrCreateDescriptionId();
    }

    @Override
    public void fillItemCategory(CreativeModeTab tab, NonNullList<ItemStack> items) {
        if (this.allowdedIn(tab)) {
            items.add(new ItemStack(this));
        }
    }

}
