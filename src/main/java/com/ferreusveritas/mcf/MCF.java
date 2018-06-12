
package com.ferreusveritas.mcf;

import java.util.ArrayList;

import com.ferreusveritas.mcf.features.IFeature;
import com.ferreusveritas.mcf.features.Security;
import com.ferreusveritas.mcf.proxy.CommonProxy;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import scala.actors.threadpool.Arrays;

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
@Mod(modid = ModConstants.MODID, version=ModConstants.VERSION,dependencies=MCF.DEPEND)
public class MCF {
	
	@Mod.Instance(ModConstants.MODID)
	public static MCF instance;
	
	public static final String DEPEND = 
			"required-after:computercraft;";
	
	@SidedProxy(clientSide = "com.ferreusveritas.mcf.proxy.ClientProxy", serverSide = "com.ferreusveritas.mcf.proxy.CommonProxy")
	public static CommonProxy proxy;
	
	public static ArrayList<IFeature> features = new ArrayList<>();
	
	/*public static final CreativeTabs mcfTab = new CreativeTabs(ModConstants.MODID) {
		@SideOnly(Side.CLIENT)
		@Override
		public ItemStack getTabIconItem() {
			return new ItemStack(ModBlocks.blockCartographer);
		}
	};*/

	public MCF() {
		features.addAll(Arrays.asList(new IFeature[] { new Security() }));
	}
	
	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		features.forEach(i -> i.preInit());
		proxy.preInit();
	}
	
	@Mod.EventHandler
	public void init(FMLInitializationEvent event) {
		features.forEach(i -> i.init());
		proxy.init();
	}
	
	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		features.forEach(i -> i.postInit());
	}
	
	@Mod.EventHandler
	public void onFMLLoadComplete(FMLLoadCompleteEvent event) {
		features.forEach(i -> i.onLoadComplete());
	}
	
	@Mod.EventBusSubscriber
	public static class RegistrationHandler {
		
		@SubscribeEvent
		public static void registerBlocks(RegistryEvent.Register<Block> event) {
			ModBlocks.registerBlocks(event.getRegistry());
		}
		
		@SubscribeEvent
		public static void registerItems(RegistryEvent.Register<Item> event) {
			ModItems.registerItems(event.getRegistry());
		}
		
		@SubscribeEvent
		public static void registerRecipes(RegistryEvent.Register<IRecipe> event) {
			features.forEach(i -> i.registerRecipes(event.getRegistry()));
		}
		
		@SubscribeEvent
		@SideOnly(Side.CLIENT)
		public static void registerModels(ModelRegistryEvent event) {
			ModModels.registerModels(event);
		}
	}
	
}