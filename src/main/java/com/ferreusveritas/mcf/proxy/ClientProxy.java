package com.ferreusveritas.mcf.proxy;

import com.ferreusveritas.dynamictrees.api.client.ModelHelper;
import com.ferreusveritas.mcf.entities.EntityItemDisplay;
import com.ferreusveritas.mcf.features.Remote;
import com.ferreusveritas.mcf.render.RenderEntityItemDisplay;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
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
		
	}

	public void registerEntityRenderers() {
		RenderingRegistry.registerEntityRenderingHandler(EntityItemDisplay.class, manager -> new RenderEntityItemDisplay(manager));
	}

	@Override
	public Minecraft getMinecraft() {
		return Minecraft.getMinecraft();
	}

	@Override
	public EntityPlayer getPlayer() {
		return getMinecraft().player;
	}

	@Override
	public void sendChatMessage(String msg, boolean addToChat) {
		msg = net.minecraftforge.event.ForgeEventFactory.onClientSendMessage(msg);
		if (msg.isEmpty()) return;
		if (addToChat) {
			getMinecraft().ingameGUI.getChatGUI().addToSentMessages(msg);
		}
		
		EntityPlayer player = getPlayer();
		
		if (net.minecraftforge.client.ClientCommandHandler.instance.executeCommand(player, msg) != 0) return;

		if(player instanceof EntityPlayerSP) {
			((EntityPlayerSP)player).sendChatMessage(msg);
		}
	}

}
