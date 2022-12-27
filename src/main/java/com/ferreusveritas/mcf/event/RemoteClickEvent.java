package com.ferreusveritas.mcf.event;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.event.entity.player.PlayerEvent;

public class RemoteClickEvent extends PlayerEvent {

    protected final Vector3d hitPos;
    protected final BlockPos blockPos;
    protected final Direction sideHit;
    protected final String remoteId;

    public RemoteClickEvent(PlayerEntity player, String remoteId, Vector3d clickPos, BlockPos blockPos, Direction sideHit) {
        super(player);
        this.remoteId = remoteId;
        this.hitPos = clickPos;
        this.blockPos = blockPos;
        this.sideHit = sideHit;
    }

    public String getRemoteId() {
        return remoteId;
    }

    public Vector3d getHitPos() {
        return hitPos;
    }

    public BlockPos getBlockPos() {
        return blockPos;
    }

    public Direction getSideHit() {
        return sideHit;
    }

}
