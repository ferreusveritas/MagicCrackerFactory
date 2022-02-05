package com.ferreusveritas.mcf.items;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;

// /give @p mcf:commandchunkring 1 0 {label:"Test Chunk Ring",command:"test",color:"#FFFF20",info:"Performs a test for chunk rings"}

public class CommandChunkRing extends CommandRing {
	
	private static Map<String, ChunkPos> playerChunkPosMap = new HashMap<>();
	private static final ChunkPos originChunk = new ChunkPos(BlockPos.ORIGIN);
	
	public CommandChunkRing() {
		super("commandchunkring", "Command Chunk Ring");
	}
	
	@Override
	public boolean shouldRun(EntityPlayer player) {
		ChunkPos lastChunkPos = playerChunkPosMap.getOrDefault(player.getName(), originChunk);
		return !new ChunkPos(player.getPosition()).equals(lastChunkPos);
	}
	
	@Override
	public void satisfyWorn(EntityPlayer player) {
		playerChunkPosMap.put(player.getName(), new ChunkPos(player.getPosition()));
	}
	
	@Override
	public void processWorn(EntityPlayer player) {
		updateAllRings(player, stack -> stack.getItem() instanceof CommandChunkRing);
	}
	
}
