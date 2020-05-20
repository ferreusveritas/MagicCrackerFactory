
package com.ferreusveritas.mcf;

import com.ferreusveritas.mcf.command.CommandProx;
import com.ferreusveritas.mcf.command.CommandSetBlockQuiet;
import com.ferreusveritas.mcf.entities.EntityItemDisplay;
import com.ferreusveritas.mcf.network.PacketRemoteClick;
import com.ferreusveritas.mcf.proxy.CommonProxy;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
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
	
	protected void setupFeatures() {
		FeatureRegistryEvent featureRegEvent = new FeatureRegistryEvent(this);
		MinecraftForge.EVENT_BUS.post(featureRegEvent);
	};
	
	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
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
		
		super.postInit(event);
	}
	
	@Mod.EventHandler
	public static void serverStarting(FMLServerStartingEvent event) {
		event.registerServerCommand(new CommandSetBlockQuiet());
		event.registerServerCommand(new CommandProx());
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
			EntityRegistry.registerModEntity(new ResourceLocation(ModConstants.MODID, "item_display"), EntityItemDisplay.class, "item_display", id++, ModConstants.MODID, 32, 1, true);
		}
	}
	
}