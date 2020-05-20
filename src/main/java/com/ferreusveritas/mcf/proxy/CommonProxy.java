package com.ferreusveritas.mcf.proxy;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;

public class CommonProxy {
	
	public void preInit() { }
	
	public void init() { }
	
	public EntityPlayer getPlayer() {
		return null;
	}

	public Minecraft getMinecraft() {
		return null;
	}

	public void sendChatMessage(String msg, boolean addToChat) { }
	
}
