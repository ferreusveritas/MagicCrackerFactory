package com.ferreusveritas.mcf.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

// /give @p mcf:commandblockring 1 0 {label:"Test Block Ring",command:"test",color:"#FFFF20",info:"Performs a test for block rings"}

public class CommandBlockRing extends CommandRing {

    private static final Map<UUID, BlockPos> PLAYER_POSITION_CACHE = new HashMap<>();

    public CommandBlockRing(Properties properties) {
        super(properties);
    }

    @Override
    public boolean shouldRun(PlayerEntity player) {
        BlockPos lastBlockPos = PLAYER_POSITION_CACHE.getOrDefault(player.getUUID(), BlockPos.ZERO);
        return !player.blockPosition().equals(lastBlockPos);
    }

    @Override
    public void satisfyWorn(PlayerEntity player) {
        PLAYER_POSITION_CACHE.put(player.getUUID(), player.blockPosition());
    }

}
