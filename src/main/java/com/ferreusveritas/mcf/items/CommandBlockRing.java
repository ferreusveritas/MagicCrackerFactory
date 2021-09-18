package com.ferreusveritas.mcf.items;

import java.util.HashMap;
import java.util.Map;

import com.ferreusveritas.mcf.tileentity.TileRemoteReceiver;

import baubles.api.BaubleType;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.Constants.NBT;

public class CommandBlockRing extends CommandRing {
	
	private static Map<String, BlockPos> playerBlockPosMap = new HashMap<>();
	
	public CommandBlockRing() {
		super("commandblockring", "Command Block Ring");
	}
	
	@Override
	public BaubleType getBaubleType(ItemStack arg0) {
		return BaubleType.RING;
	}
	
	public NBTTagCompound getNBT(ItemStack itemStack) {
		return itemStack.hasTagCompound() ? itemStack.getTagCompound() : new NBTTagCompound();
	}
	
	@Override
	public void onWornTick(ItemStack itemstack, EntityLivingBase entityLiving) {
		
		if(!entityLiving.world.isRemote && entityLiving instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) entityLiving;
			BlockPos blockPos = entityLiving.getPosition();
			
			String playerName = player.getName();
			BlockPos lastBlockPos = playerBlockPosMap.getOrDefault(playerName, BlockPos.ORIGIN);
			
			if(!blockPos.equals(lastBlockPos)) {
				playerBlockPosMap.put(playerName, blockPos);
				
				if(itemstack.hasTagCompound()) {
					NBTTagCompound nbt = itemstack.getTagCompound();
					if(nbt.hasKey("command", NBT.TAG_STRING)) {
						String command = nbt.getString("command");
						if(!command.isEmpty()) {
							if(player instanceof EntityPlayer) {
								TileRemoteReceiver.broadcastRingEvents((EntityPlayer) player, command);
							}
						}
					}
				}
			}
		}
	}
	
}
