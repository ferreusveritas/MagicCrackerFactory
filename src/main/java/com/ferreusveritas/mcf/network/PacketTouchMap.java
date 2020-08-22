package com.ferreusveritas.mcf.network;

import com.ferreusveritas.mcf.MCF;
import com.ferreusveritas.mcf.event.TouchMapEvent;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class PacketTouchMap implements IMessage, IMessageHandler<PacketTouchMap, IMessage> {
	
	private Vec3d hitPos;
	private BlockPos blockPos;
	private EnumFacing sideHit;
	
	public PacketTouchMap() { }
	
	public PacketTouchMap(Vec3d hitPos, BlockPos blockPos, EnumFacing sideHit) {
		this.hitPos = hitPos;
		this.blockPos = blockPos;
		this.sideHit = sideHit;
	}
	
	@Override
	public IMessage onMessage(PacketTouchMap message, MessageContext ctx) {
		EntityPlayer player = ctx.side == Side.SERVER ? ctx.getServerHandler().player : MCF.proxy.getPlayer();
		TouchMapEvent touchMapEvent = new TouchMapEvent(player, player.getHeldItemMainhand(), message.hitPos, message.blockPos, message.sideHit);
		MinecraftForge.EVENT_BUS.post(touchMapEvent);
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
