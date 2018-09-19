package com.ferreusveritas.mcf.network;

import com.ferreusveritas.mcf.event.RemoteClickEvent;
import com.ferreusveritas.mcf.items.UniversalRemote;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class PacketRemoteClick implements IMessage, IMessageHandler<PacketRemoteClick, IMessage> {
	
	private Vec3d hitPos;
	private BlockPos blockPos;
	private EnumFacing sideHit;
	
	public PacketRemoteClick() { }
	
	public PacketRemoteClick(Vec3d hitPos, BlockPos blockPos, EnumFacing sideHit) {
		this.hitPos = hitPos;
		this.blockPos = blockPos;
		this.sideHit = sideHit;
	}
	
	@Override
	public IMessage onMessage(PacketRemoteClick message, MessageContext ctx) {
		EntityPlayer player = ctx.side == Side.SERVER ? ctx.getServerHandler().player : Minecraft.getMinecraft().player;
		ItemStack remoteItem = player.getHeldItemMainhand();
		if(remoteItem.getItem() instanceof UniversalRemote) {
			RemoteClickEvent removeClickEvent = new RemoteClickEvent(player, remoteItem, hitPos, blockPos, sideHit);
			MinecraftForge.EVENT_BUS.post(removeClickEvent);
		}
		
		System.out.println("Remote Message Received: " + player + " " + hitPos);
		
		return null;
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		double hx = buf.readDouble();
		double hy = buf.readDouble();
		double hz = buf.readDouble();
		hitPos = new Vec3d(hx, hy, hz);
		
		int bx = buf.readInt();
		int by = buf.readInt();
		int bz = buf.readInt();
		blockPos = new BlockPos(bx, by, bz);
		
		sideHit = EnumFacing.getFront(buf.readByte());
	}
	
	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeDouble(hitPos.x);
		buf.writeDouble(hitPos.y);
		buf.writeDouble(hitPos.z);
		
		buf.writeInt(blockPos.getX());
		buf.writeInt(blockPos.getY());
		buf.writeInt(blockPos.getZ());
		
		buf.writeByte(sideHit.ordinal());
	}
	
}
