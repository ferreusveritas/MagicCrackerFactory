package com.ferreusveritas.mcf.network;

import com.ferreusveritas.mcf.event.RemoteClickEvent;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class PacketRemoteClick implements IMessage, IMessageHandler<PacketRemoteClick, IMessage> {
	
	Vec3d clickPos;
	
	public PacketRemoteClick() {
	}

	public PacketRemoteClick(Vec3d pos) {
		clickPos = pos;
	}
	
	@Override
	public IMessage onMessage(PacketRemoteClick message, MessageContext ctx) {
		EntityPlayer player = ctx.side == Side.SERVER ? ctx.getServerHandler().player : Minecraft.getMinecraft().player;
		ItemStack remoteItem = player.getHeldItemMainhand();
		RemoteClickEvent removeClickEvent = new RemoteClickEvent(player, remoteItem, clickPos);
		MinecraftForge.EVENT_BUS.post(removeClickEvent);
		
		System.out.println("Remote Message Received: " + player + " " + clickPos);
		
		return null;
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		double x = buf.readDouble();
		double y = buf.readDouble();
		double z = buf.readDouble();
		clickPos = new Vec3d(x, y, z);
	}
	
	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeDouble(clickPos.x);
		buf.writeDouble(clickPos.y);
		buf.writeDouble(clickPos.z);
	}
	
}
