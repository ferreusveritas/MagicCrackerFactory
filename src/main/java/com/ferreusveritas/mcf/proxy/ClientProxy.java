package com.ferreusveritas.mcf.proxy;

import com.ferreusveritas.mcf.client.ModelHelper;
import com.ferreusveritas.mcf.entities.EntityCommandPotion;
import com.ferreusveritas.mcf.entities.EntityItemDisplay;
import com.ferreusveritas.mcf.features.Remote;
import com.ferreusveritas.mcf.features.Rings;
import com.ferreusveritas.mcf.render.RenderEntityItemDisplay;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderPotion;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

public class ClientProxy extends CommonProxy {
	
	public void preInit() {
		registerEntityRenderers();
	}
	
	@Override
	public void init() {
		super.init();
		registerColorHandlers();
	}
	
	public void registerColorHandlers() {
		
		//Register Universal Remote Colorizer
		ModelHelper.regColorHandler(Remote.universalRemote, (stack, tintIndex) -> Remote.universalRemote.getColor(stack, tintIndex));
		
		//Register Command Potion Colorizer
		ModelHelper.regColorHandler(Remote.commandPotion, (stack, tintIndex) -> Remote.commandPotion.getColor(stack, tintIndex));
		ModelHelper.regColorHandler(Remote.commandSplashPotion, (stack, tintIndex) -> Remote.commandSplashPotion.getColor(stack, tintIndex));
		
		//Register Command Ring Colorizers
		ModelHelper.regColorHandler(Rings.commandChunkRing, (stack, tintIndex) -> Rings.commandChunkRing.getColor(stack, tintIndex));
		ModelHelper.regColorHandler(Rings.commandBlockRing, (stack, tintIndex) -> Rings.commandBlockRing.getColor(stack, tintIndex));
		
	}
	
	public void registerEntityRenderers() {
		RenderingRegistry.registerEntityRenderingHandler(EntityItemDisplay.class, manager -> new RenderEntityItemDisplay(manager));
		RenderingRegistry.registerEntityRenderingHandler(EntityCommandPotion.class, manager -> new RenderPotion(manager, Minecraft.getMinecraft().getRenderItem()));
	}
	
	@Override
	public Minecraft getMinecraft() {
		return Minecraft.getMinecraft();
	}
	
	@Override
	public EntityPlayer getPlayer() {
		return getMinecraft().player;
	}
	
}
