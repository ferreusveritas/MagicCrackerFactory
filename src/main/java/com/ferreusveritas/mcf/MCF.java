
package com.ferreusveritas.mcf;

import com.ferreusveritas.mcf.command.CommandProx;
import com.ferreusveritas.mcf.command.CommandSetBlockQuiet;
import com.ferreusveritas.mcf.entities.EntityCommandPotion;
import com.ferreusveritas.mcf.entities.EntityItemDisplay;
import com.ferreusveritas.mcf.network.CommsThread;
import com.ferreusveritas.mcf.network.PacketRemoteClick;
import com.ferreusveritas.mcf.network.PacketTouchMap;
import com.ferreusveritas.mcf.proxy.CommonProxy;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.relauncher.Side;

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
*
*/
@Mod(modid = ModConstants.MODID, version=ModConstants.VERSION, dependencies=ModConstants.DEPENDENCIES)
public class MCF extends FeatureableMod {
	
	@Mod.Instance(ModConstants.MODID)
	public static MCF instance;
	
	public static final SimpleNetworkWrapper network = NetworkRegistry.INSTANCE.newSimpleChannel(ModConstants.MODID);
	
	@SidedProxy(clientSide = "com.ferreusveritas.mcf.proxy.ClientProxy", serverSide = "com.ferreusveritas.mcf.proxy.CommonProxy")
	public static CommonProxy proxy;
	
	private static CommsThread commsThread;
	
	// The port to listen on for incoming requests.
	public static int LISTEN_PORT = 60000;
	
	protected void setupFeatures() {
		FeatureRegistryEvent featureRegEvent = new FeatureRegistryEvent(this);
		MinecraftForge.EVENT_BUS.post(featureRegEvent);
	};
	
	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		Configuration cfg = new Configuration(event.getSuggestedConfigurationFile());
		try {
			cfg.load();
			LISTEN_PORT = cfg.get(Configuration.CATEGORY_GENERAL, "listenPort", LISTEN_PORT).getInt();
		}
		catch (Exception e) {
			FMLLog.log.warn(e.toString(), "Magic Cracker Factory has a problem loading its configuration");
		}
		finally	{
			if (cfg.hasChanged()) {
				cfg.save();
			}
		}
		
		setupFeatures();
		super.preInit(event);
		proxy.preInit();
	}
	
	@Mod.EventHandler
	public void init(FMLInitializationEvent event) {
		super.init(event);
		proxy.init();
	}
	
	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		int disc = 0;
		network.registerMessage(PacketRemoteClick.class, PacketRemoteClick.class, disc++, Side.SERVER);
		network.registerMessage(PacketTouchMap.class, PacketTouchMap.class, disc++, Side.SERVER);
		
		super.postInit(event);
	}
	
	@Mod.EventHandler
	public static void serverStarting(FMLServerStartingEvent event) {
		event.registerServerCommand(new CommandSetBlockQuiet());
		event.registerServerCommand(new CommandProx());
		
		if(LISTEN_PORT > 0) {
			commsThread = new CommsThread(LISTEN_PORT);
			CommsThread.setInstance(commsThread);
			commsThread.start();
		}
	}
	
	@Mod.EventHandler
	public void onLoadComplete(FMLLoadCompleteEvent event) {
		super.onLoadComplete(event);
	}
	
	@Mod.EventBusSubscriber
	public static class RegistrationHandler {
		@SubscribeEvent
		public static void registerEntities(RegistryEvent.Register<EntityEntry> event) {
			int id = 0;
			EntityRegistry.registerModEntity(new ResourceLocation(ModConstants.MODID, "item_display"), EntityItemDisplay.class, "item_display", id++, ModConstants.MODID, 32, 1, false);
			EntityRegistry.registerModEntity(new ResourceLocation(ModConstants.MODID, "command_potion"), EntityCommandPotion.class, "command_potion", id++, ModConstants.MODID, 64, 10, true);
		}
	}

	@Mod.EventHandler
	public void serverStopping(FMLServerStoppingEvent event) {
		if(LISTEN_PORT > 0) {
			commsThread.shutdown();
			try {
				commsThread.join();
			}
			catch (InterruptedException e) {}//We don't care, we're shutting down anyway.
		}
	}
	
}