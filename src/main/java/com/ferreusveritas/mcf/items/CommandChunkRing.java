package com.ferreusveritas.mcf.items;

import com.ferreusveritas.mcf.MCF;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.ChunkPos;
import net.minecraftforge.common.util.Constants.NBT;

public class CommandChunkRing extends CommandRing {
	
	ChunkPos lastChunkPos = null;
	
	public CommandChunkRing() {
		super("commandchunkring");
	}
	
	@Override
	public void onWornTick(ItemStack itemstack, EntityLivingBase player) {
		if(player.world.isRemote) { //Commands are executed client side
			ChunkPos chunkPos = new ChunkPos(player.chunkCoordX, player.chunkCoordZ);
			
			if(!chunkPos.equals(lastChunkPos)) {
				lastChunkPos = chunkPos;
				
				String command = "";
				
				if(itemstack.hasTagCompound()) {
					NBTTagCompound nbt = itemstack.getTagCompound();
					if(nbt.hasKey("command", NBT.TAG_STRING)) {
						command = nbt.getString("command");
					}
				}
				
				if(!command.isEmpty()) {
					MCF.proxy.sendChatMessage(command, false);
				}
			}
		}
	}
	
}
