package com.ferreusveritas.mcf.network;

import com.ferreusveritas.mcf.event.TouchMapEvent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class ServerBoundTouchMapMessage implements Message {

    public static final Decoder DECODER = new Decoder();

    private final Vector3d hitPos;
    private final BlockPos blockPos;
    private final Direction sideHit;

    public ServerBoundTouchMapMessage(Vector3d hitPos, BlockPos blockPos, Direction sideHit) {
        this.hitPos = hitPos;
        this.blockPos = blockPos;
        this.sideHit = sideHit;
    }

    @Override
    public boolean handle(Supplier<NetworkEvent.Context> context) {
        PlayerEntity player = context.get().getSender();
        TouchMapEvent touchMapEvent = new TouchMapEvent(player, player.getMainHandItem(), hitPos, blockPos, sideHit);
        MinecraftForge.EVENT_BUS.post(touchMapEvent);
        return true;
    }

    @Override
    public void toBytes(PacketBuffer buffer) {
        buffer.writeDouble(hitPos.x);
        buffer.writeDouble(hitPos.y);
        buffer.writeDouble(hitPos.z);

        buffer.writeInt(blockPos.getX());
        buffer.writeInt(blockPos.getY());
        buffer.writeInt(blockPos.getZ());

        buffer.writeByte(sideHit.get3DDataValue());
    }

    private static final class Decoder implements Message.Decoder<ServerBoundTouchMapMessage> {
        @Override
        public ServerBoundTouchMapMessage fromBytes(PacketBuffer buffer) {
            double hx = buffer.readDouble();
            double hy = buffer.readDouble();
            double hz = buffer.readDouble();
            Vector3d hitPos = new Vector3d(hx, hy, hz);

            int bx = buffer.readInt();
            int by = buffer.readInt();
            int bz = buffer.readInt();
            BlockPos blockPos = new BlockPos(bx, by, bz);

            Direction sideHit = Direction.from3DDataValue(buffer.readByte());
            return new ServerBoundTouchMapMessage(hitPos, blockPos, sideHit);
        }
    }

}
