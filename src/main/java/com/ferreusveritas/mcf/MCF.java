
package com.ferreusveritas.mcf;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import proxy.CommonProxy;

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
@Mod(modid = ModConstants.MODID, version=ModConstants.VERSION,dependencies="")
public class MCF {

	@Mod.Instance(ModConstants.MODID)
	public static MCF instance;
	
	@SidedProxy(clientSide = "com.ferreusveritas.mcf.proxy.ClientProxy", serverSide = "com.ferreusveritas.mcf.proxy.CommonProxy")
	public static CommonProxy proxy;
	
	public static final CreativeTabs mcfTab = new CreativeTabs(ModConstants.MODID) {
        @SideOnly(Side.CLIENT)
		@Override
		public ItemStack getTabIconItem() {
			return new ItemStack(Items.NETHER_STAR);
		}
	};

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		
		//ModConfigs.preInit(event);//Naturally this comes first so we can react to settings
		
		ModBlocks.preInit();
		//ModItems.preInit();
		//ModTrees.preInit();
		
		proxy.preInit();
	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent event) {
		proxy.init();
	}
	
	@Mod.EventBusSubscriber
	public static class RegistrationHandler {
		
		@SubscribeEvent
		public static void registerBlocks(RegistryEvent.Register<Block> event) {
			ModBlocks.registerBlocks(event.getRegistry());
		}
		
		@SubscribeEvent
		public static void registerItems(RegistryEvent.Register<Item> event) {
			//ModItems.registerItems(event.getRegistry());
		}
		
		@SubscribeEvent
		public static void registerRecipes(RegistryEvent.Register<IRecipe> event) {
			//ModRecipes.registerRecipes(event.getRegistry());
		}
		
	}
	
}