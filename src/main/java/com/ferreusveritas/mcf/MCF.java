package com.ferreusveritas.mcf;

import com.ferreusveritas.mcf.client.ClientSetup;
import com.ferreusveritas.mcf.command.ProxCommand;
import com.ferreusveritas.mcf.event.SecurityHandler;
import com.ferreusveritas.mcf.network.CommsThread;
import com.ferreusveritas.mcf.network.Networking;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

/**
 * <p><pre><tt><b>
 *  ╭─────────────────╮
 *  │                 │
 *  │                 │
 *  │                 │
 *  │                 │
 *  │                 │
 *  │                 │
 *  │                 │
 *  │                 │
 *  ╞═════════════════╡
 *  │       MCF       │
 *  ╰─────────────────╯
 * </b></tt></pre></p>
 *
 * <p>
 * 2018 Ferreusveritas
 * </p>
 */
@Mod(MCF.MOD_ID)
public class MCF {

    public static final String MOD_ID = "mcf";

    private static CommsThread commsThread;

    public MCF() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        Registry.setup(modBus);
        modBus.addListener(this::clientSetup);
        modBus.addListener(this::commonSetup);

        MinecraftForge.EVENT_BUS.addListener(this::serverStarting);
        MinecraftForge.EVENT_BUS.addListener(this::serverStopping);

        MinecraftForge.EVENT_BUS.register(new SecurityHandler());
    }

    public static ResourceLocation location(String path) {
        return new ResourceLocation(MOD_ID, path);
    }

    private void clientSetup(FMLClientSetupEvent event) {
        ClientSetup.registerEntityRenderers();
        ClientSetup.registerRenderTypes();
        event.enqueueWork(ClientSetup::registerColorHandlers);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        Networking.createChannel();
        Networking.registerPackets();
    }

    private void serverStarting(FMLServerStartingEvent event) {
        int port = MCFConfigs.LISTEN_PORT.get();
        if (port > 0) {
            commsThread = new CommsThread(port);
            CommsThread.setInstance(commsThread);
            commsThread.start();
        }
    }

    private void registerCommands(RegisterCommandsEvent event) {
        ProxCommand.register(event.getDispatcher());
    }

    private void serverStopping(FMLServerStoppingEvent event) {
        if (MCFConfigs.LISTEN_PORT.get() > 0) {
            commsThread.shutdown();
            try {
                commsThread.join();
            } catch (InterruptedException e) {
            }//We don't care, we're shutting down anyway.
        }
    }

}