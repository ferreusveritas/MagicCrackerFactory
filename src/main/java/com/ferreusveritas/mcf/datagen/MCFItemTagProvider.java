package com.ferreusveritas.mcf.datagen;

import com.ferreusveritas.mcf.Registry;
import net.minecraft.data.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.ItemTagsProvider;
import net.minecraft.item.Item;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;
import net.minecraftforge.common.data.ExistingFileHelper;

import javax.annotation.Nullable;

public class MCFItemTagProvider extends ItemTagsProvider {

    public MCFItemTagProvider(DataGenerator generator, BlockTagsProvider blockTagsProvider, String modId, @Nullable ExistingFileHelper existingFileHelper) {
        super(generator, blockTagsProvider, modId, existingFileHelper);
    }

    @Override
    protected void addTags() {
        this.tag(Tags.RING).add(Registry.COMMAND_BLOCK_RING.get(), Registry.COMMAND_CHUNK_RING.get());
    }

    private static final class Tags {

        private static final ITag.INamedTag<Item> RING = bind("curios:ring");

        private static ITag.INamedTag<Item> bind(String identifier) {
            return ItemTags.bind(identifier);
        }

    }

}
