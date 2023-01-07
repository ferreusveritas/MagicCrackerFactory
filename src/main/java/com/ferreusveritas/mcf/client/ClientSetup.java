package com.ferreusveritas.mcf.client;

import com.ferreusveritas.mcf.Registry;
import com.ferreusveritas.mcf.entity.render.ItemDisplayEntityRenderer;
import com.ferreusveritas.mcf.item.ColoredItem;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import org.apache.logging.log4j.LogManager;

@OnlyIn(Dist.CLIENT)
public final class ClientSetup {

    public static void registerEntityRenderers() {
        RenderingRegistry.registerEntityRenderingHandler(Registry.ITEM_DISPLAY_ENTITY.get(), ItemDisplayEntityRenderer::new);
//        RenderingRegistry.registerEntityRenderingHandler(Registry.COMMAND_POTION_ENTITY.get(), manager -> new RenderPotion(manager, Minecraft.getMinecraft().getRenderItem()));
    }

    public static void registerRenderTypes() {
        RenderTypeLookup.setRenderLayer(Registry.LIGHT_BLOCK.get(), RenderType.cutout());
        RenderTypeLookup.setRenderLayer(Registry.MAP_GUARD_BLOCK.get(), RenderType.cutout());
    }

    public static void registerColorHandlers() {
        // Register Universal Remote Colorizer
        registerColorHandler(Registry.UNIVERSAL_REMOTE.get());

        // Register Command Potion Colorizer
        registerColorHandler(Registry.COMMAND_POTION.get());
        registerColorHandler(Registry.COMMAND_SPLASH_POTION.get());

        // Register Command Ring Colorizers
        registerColorHandler(Registry.COMMAND_CHUNK_RING.get());
        registerColorHandler(Registry.COMMAND_BLOCK_RING.get());
    }

    private static <I extends Item & ColoredItem> void registerColorHandler(I item) {
        ModelHelper.registerColorHandler(item, (stack, tintIndex) -> item.getColor(stack, tintIndex));
    }


}
