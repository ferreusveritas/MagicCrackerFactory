package com.ferreusveritas.mcf.network;

import com.ferreusveritas.mcf.event.RemoteClickEvent;
import com.ferreusveritas.mcf.item.UniversalRemote;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ServerBoundRemoteClickMessage implements Message {

    public static final Decoder DECODER = new Decoder();

    private final Vec3 hitPos;
    private final BlockPos blockPos;
    private final Direction sideHit;

    public ServerBoundRemoteClickMessage(Vec3 hitPos, BlockPos blockPos, Direction sideHit) {
        this.hitPos = hitPos;
        this.blockPos = blockPos;
        this.sideHit = sideHit;
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

    @Override
    public boolean handle(Supplier<NetworkEvent.Context> context) {
        Player player = context.get().getSender();
        ItemStack heldItem = player.getMainHandItem();
        if (heldItem.getItem() instanceof UniversalRemote remote) {
            String remoteId = remote.getId(heldItem);
            RemoteClickEvent removeClickEvent = new RemoteClickEvent(player, remoteId, hitPos, blockPos, sideHit);
            MinecraftForge.EVENT_BUS.post(removeClickEvent);
            return true;
        }
        return false;
    }

    private static final class Decoder implements Message.Decoder<ServerBoundRemoteClickMessage> {
        @Override
        public ServerBoundRemoteClickMessage fromBytes(FriendlyByteBuf buffer) {
            double hx = buffer.readDouble();
            double hy = buffer.readDouble();
            double hz = buffer.readDouble();
            Vec3 hitPos = new Vec3(hx, hy, hz);

            int bx = buffer.readInt();
            int by = buffer.readInt();
            int bz = buffer.readInt();
            BlockPos blockPos = new BlockPos(bx, by, bz);

            Direction sideHit = Direction.from3DDataValue(buffer.readByte());
            return new ServerBoundRemoteClickMessage(hitPos, blockPos, sideHit);
        }
    }

}
