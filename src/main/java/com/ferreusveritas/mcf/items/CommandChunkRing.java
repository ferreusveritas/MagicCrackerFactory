package com.ferreusveritas.mcf.items;

import java.util.HashMap;
import java.util.Map;

import com.ferreusveritas.mcf.tileentity.TileRemoteReceiver;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraftforge.common.util.Constants.NBT;

public class CommandChunkRing extends CommandRing {
	
	private static Map<String, ChunkPos> playerChunkPosMap = new HashMap<>();
	private static final ChunkPos originChunk = new ChunkPos(BlockPos.ORIGIN);
	
	public CommandChunkRing() {
		super("commandchunkring", "Command Chunk Ring");
	}
	
	@Override
	public void onWornTick(ItemStack itemstack, EntityLivingBase entityLiving) {
		if(!entityLiving.world.isRemote) {
			
			EntityPlayer player = (EntityPlayer) entityLiving;
			ChunkPos chunkPos = new ChunkPos(entityLiving.getPosition());
			
			String playerName = player.getName();
			ChunkPos lastChunkPos = playerChunkPosMap.getOrDefault(playerName, originChunk);
			
			if(!chunkPos.equals(lastChunkPos)) {
				lastChunkPos = chunkPos;
				
				if(itemstack.hasTagCompound()) {
					NBTTagCompound nbt = itemstack.getTagCompound();
					if(nbt.hasKey("command", NBT.TAG_STRING)) {
						String command = nbt.getString("command");
						if(!command.isEmpty()) {
							TileRemoteReceiver.broadcastRingEvents((EntityPlayer) player, command);
						}
					}
				}
			}
		}
	}
	
}
