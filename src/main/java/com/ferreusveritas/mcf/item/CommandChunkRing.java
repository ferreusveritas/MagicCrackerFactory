package com.ferreusveritas.mcf.item;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

// /give @p mcf:commandchunkring 1 0 {label:"Test Chunk Ring",command:"test",color:"#FFFF20",info:"Performs a test for chunk rings"}

public class CommandChunkRing extends CommandRing {

    private static final Map<UUID, ChunkPos> PLAYER_POSITION_CACHE = new HashMap<>();
    private static final ChunkPos ZERO_CHUNK_POS = new ChunkPos(BlockPos.ZERO);

    public CommandChunkRing(Properties properties) {
        super(properties);
    }

    @Override
    public boolean shouldRun(Player player) {
        ChunkPos lastChunkPos = PLAYER_POSITION_CACHE.getOrDefault(player.getUUID(), ZERO_CHUNK_POS);
        return !new ChunkPos(player.blockPosition()).equals(lastChunkPos);
    }

    @Override
    public void satisfyWorn(Player player) {
        PLAYER_POSITION_CACHE.put(player.getUUID(), new ChunkPos(player.blockPosition()));
    }

}
