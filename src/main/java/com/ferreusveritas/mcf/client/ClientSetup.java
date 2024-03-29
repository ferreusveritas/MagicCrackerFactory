package com.ferreusveritas.mcf.client;

import com.ferreusveritas.mcf.Registry;
import com.ferreusveritas.mcf.entity.render.ItemDisplayEntityRenderer;
import com.ferreusveritas.mcf.item.ColoredItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.entity.SpriteRenderer;
import net.minecraft.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

@OnlyIn(Dist.CLIENT)
public final class ClientSetup {

    public static void registerEntityRenderers() {
        RenderingRegistry.registerEntityRenderingHandler(Registry.ITEM_DISPLAY_ENTITY.get(), ItemDisplayEntityRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(Registry.COMMAND_POTION_ENTITY.get(), manager -> new SpriteRenderer<>(manager, Minecraft.getInstance().getItemRenderer()));
    }

    public static void registerRenderTypes() {
        RenderTypeLookup.setRenderLayer(Registry.LIGHT_BLOCK.get(), RenderType.cutout());
        RenderTypeLookup.setRenderLayer(Registry.MAP_GUARD_BLOCK.get(), RenderType.cutout());
    }

    public static void registerColorHandlers() {
        registerColorHandler(Registry.UNIVERSAL_REMOTE.get());

        registerColorHandler(Registry.COMMAND_POTION.get());
        registerColorHandler(Registry.COMMAND_SPLASH_POTION.get());

        registerColorHandler(Registry.COMMAND_CHUNK_RING.get());
        registerColorHandler(Registry.COMMAND_BLOCK_RING.get());
    }

    private static <I extends Item & ColoredItem> void registerColorHandler(I item) {
        ModelHelper.registerColorHandler(item, (stack, tintIndex) -> item.getColor(stack, tintIndex));
    }


}
