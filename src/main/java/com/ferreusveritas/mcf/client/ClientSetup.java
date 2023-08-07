package com.ferreusveritas.mcf.client;

import com.ferreusveritas.mcf.MCF;
import com.ferreusveritas.mcf.Registry;
import com.ferreusveritas.mcf.entity.renderer.ItemDisplayEntityRenderer;
import com.ferreusveritas.mcf.item.ColoredItem;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = MCF.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ClientSetup {

    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(Registry.ITEM_DISPLAY_ENTITY.get(), ItemDisplayEntityRenderer::new);
        event.registerEntityRenderer(Registry.COMMAND_POTION_ENTITY.get(), ThrownItemRenderer::new);
    }

    public static void registerRenderTypes() {
        ItemBlockRenderTypes.setRenderLayer(Registry.MAP_GUARD_BLOCK.get(), RenderType.cutout());
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
