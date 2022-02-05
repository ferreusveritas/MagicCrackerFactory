package com.ferreusveritas.mcf.items;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;

// /give @p mcf:commandblockring 1 0 {label:"Test Block Ring",command:"test",color:"#FFFF20",info:"Performs a test for block rings"}

public class CommandBlockRing extends CommandRing {
	
	private static Map<String, BlockPos> playerBlockPosMap = new HashMap<>();
	
	public CommandBlockRing() {
		super("commandblockring", "Command Block Ring");
	}
	
	@Override
	public boolean shouldRun(EntityPlayer player) {
		BlockPos lastBlockPos = playerBlockPosMap.getOrDefault(player.getName(), BlockPos.ORIGIN);
		return !player.getPosition().equals(lastBlockPos);
	}
	
	@Override
	public void satisfyWorn(EntityPlayer player) {
		playerBlockPosMap.put(player.getName(), player.getPosition());
	}
	
	@Override
	public void processWorn(EntityPlayer player) {
		updateAllRings(player, stack -> stack.getItem() instanceof CommandBlockRing);
	}
	
}
