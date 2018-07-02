
package com.ferreusveritas.mcf;

import com.ferreusveritas.mcf.features.Cartographer;
import com.ferreusveritas.mcf.features.Dendrocoil;
import com.ferreusveritas.mcf.features.Security;
import com.ferreusveritas.mcf.features.Sentinel;
import com.ferreusveritas.mcf.features.Terraformer;
import com.ferreusveritas.mcf.proxy.CommonProxy;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

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
	
	@SidedProxy(clientSide = "com.ferreusveritas.mcf.proxy.ClientProxy", serverSide = "com.ferreusveritas.mcf.proxy.CommonProxy")
	public static CommonProxy proxy;
	
	protected void setupFeatures() {
		addFeatures(
			new Security(),
			new Cartographer(),
			new Terraformer(),
			new Sentinel(),
			new Dendrocoil()
		);
	};
	
	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		setupFeatures();
		super.preInit(event);
	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent event) {
		super.init(event);
	}

	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		super.postInit(event);
	}

	@Mod.EventHandler
	public void onLoadComplete(FMLLoadCompleteEvent event) {
		super.onLoadComplete(event);
	}
	
}