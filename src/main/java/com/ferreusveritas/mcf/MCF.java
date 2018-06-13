
package com.ferreusveritas.mcf;

import com.ferreusveritas.mcf.features.Cartographer;
import com.ferreusveritas.mcf.features.Security;
import com.ferreusveritas.mcf.features.Sentinel;
import com.ferreusveritas.mcf.features.Terraformer;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLStateEvent;

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
	public void stateEvent(FMLStateEvent event) { super.stateEvent(event); }
	
}