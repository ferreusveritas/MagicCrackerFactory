package com.ferreusveritas.mcf.network;

import com.ferreusveritas.mcf.event.TouchMapEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ServerBoundTouchMapMessage implements Message {

    public static final Decoder DECODER = new Decoder();

    private final Vec3 hitPos;
    private final BlockPos blockPos;
    private final Direction sideHit;

    public ServerBoundTouchMapMessage(Vec3 hitPos, BlockPos blockPos, Direction sideHit) {
        this.hitPos = hitPos;
        this.blockPos = blockPos;
        this.sideHit = sideHit;
    }
  
    @Override
    public boolean handle(Supplier<NetworkEvent.Context> context) {
        Player player = context.get().getSender();
        TouchMapEvent touchMapEvent = new TouchMapEvent(player, player.getMainHandItem(), hitPos, blockPos, sideHit);
        MinecraftForge.EVENT_BUS.post(touchMapEvent);
        return true;
    }

    @Override
    public void toBytes(FriendlyByteBuf buffer) {
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
        public ServerBoundTouchMapMessage fromBytes(FriendlyByteBuf buffer) {
            double hx = buffer.readDouble();
            double hy = buffer.readDouble();
            double hz = buffer.readDouble();
            Vec3 hitPos = new Vec3(hx, hy, hz);

            int bx = buffer.readInt();
            int by = buffer.readInt();
            int bz = buffer.readInt();
            BlockPos blockPos = new BlockPos(bx, by, bz);

            Direction sideHit = Direction.from3DDataValue(buffer.readByte());
            return new ServerBoundTouchMapMessage(hitPos, blockPos, sideHit);
        }
    }

}
