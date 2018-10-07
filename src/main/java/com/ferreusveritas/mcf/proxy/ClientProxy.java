package com.ferreusveritas.mcf.proxy;

import com.ferreusveritas.dynamictrees.api.client.ModelHelper;
import com.ferreusveritas.mcf.entities.EntityItemDisplay;
import com.ferreusveritas.mcf.features.Remote;
import com.ferreusveritas.mcf.render.RenderEntityItemDisplay;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

public class ClientProxy extends CommonProxy {
	
	public CreativeTabs findCreativeTab(String label) {
		for(CreativeTabs iTab: CreativeTabs.CREATIVE_TAB_ARRAY) {
			if(iTab.getTabLabel().equals(label)) {
				return iTab;
			}
		}
		return null;
	}
	
	public void preInit() {
		registerEntityRenderers();
	}
	
	@Override
	public void init() {
		super.init();
		registerColorHandlers();
	}
	
	public void registerColorHandlers() {
		
		//Register Universal Remote and Colorizer
		ModelHelper.regColorHandler(Remote.universalRemote, new IItemColor() {
			@Override
			public int colorMultiplier(ItemStack stack, int tintIndex) {
				return tintIndex == 1 ? Remote.universalRemote.getColor(stack) : 0xFFFFFFFF;
			}
		});
		
	}
	
	public void registerEntityRenderers() {
		RenderingRegistry.registerEntityRenderingHandler(EntityItemDisplay.class, new RenderEntityItemDisplay.Factory());
	}
	
	@Override
	public EntityPlayer getPlayer() {
		return Minecraft.getMinecraft().player;
	}
}
