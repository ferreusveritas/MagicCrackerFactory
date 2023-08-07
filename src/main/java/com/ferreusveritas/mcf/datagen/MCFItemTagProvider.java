package com.ferreusveritas.mcf.datagen;

import com.ferreusveritas.mcf.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
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

        private static final TagKey<Item> RING = bind("curios:ring");

        private static TagKey<Item> bind(String name) {
            return TagKey.create(net.minecraft.core.Registry.ITEM_REGISTRY, new ResourceLocation(name));
        }

    }

}
