package com.ferreusveritas.mcf.event;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.player.PlayerEvent;

public class RemoteClickEvent extends PlayerEvent {

    protected final Vec3 hitPos;
    protected final BlockPos blockPos;
    protected final Direction sideHit;
    protected final String remoteId;

    public RemoteClickEvent(Player player, String remoteId, Vec3 hitPos, BlockPos blockPos, Direction sideHit) {
        super(player);
        this.remoteId = remoteId;
        this.hitPos = hitPos;
        this.blockPos = blockPos;
        this.sideHit = sideHit;
    }

    public String getRemoteId() {
        return remoteId;
    }

    public Vec3 getHitPos() {
        return hitPos;
    }

    public BlockPos getBlockPos() {
        return blockPos;
    }

    public Direction getSideHit() {
        return sideHit;
    }

}
