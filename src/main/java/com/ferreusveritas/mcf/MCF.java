
package com.ferreusveritas.mcf;

import com.ferreusveritas.mcf.features.Cartographer;
import com.ferreusveritas.mcf.features.Security;
import com.ferreusveritas.mcf.features.Sentinal;
import com.ferreusveritas.mcf.features.Terraformer;
import com.ferreusveritas.mcf.proxy.CommonProxy;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;

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
	
	@Override
	protected void setupFeatures() {
		addFeatures(new Security(), new Cartographer(), new Terraformer(), new Sentinal());
	};
	
}