
package com.ferreusveritas.mcf;

import com.ferreusveritas.mcf.features.Cartographer;
import com.ferreusveritas.mcf.features.Security;
import com.ferreusveritas.mcf.features.Sentinel;
import com.ferreusveritas.mcf.features.Terraformer;

import net.minecraftforge.fml.common.Mod;
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
	
	@Override
	protected void setupFeatures() {
		addFeatures(
			new Security(),
			new Cartographer(),
			new Terraformer(),
			new Sentinel()
		);
	};
	
	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
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