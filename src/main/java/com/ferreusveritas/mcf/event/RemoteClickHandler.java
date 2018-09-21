package com.ferreusveritas.mcf.event;

import com.ferreusveritas.mcf.blocks.IRemoteActivatable;
import com.ferreusveritas.mcf.tileentity.TileRemoteReceiver;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
public class RemoteClickHandler {
	
	@SubscribeEvent
	public static void onRemoteClickEvent(RemoteClickEvent event) {
		EntityPlayer player = event.getEntityPlayer();
		World world = event.getEntityPlayer().world;
		BlockPos blockPos = event.getBlockPos();
		Vec3d hitPos = event.getHitPos();
		EnumFacing side = event.getSideHit();
		
		if(!world.isRemote) {
			TileRemoteReceiver.broadcastRemoteEvents(player, event.getRemoteId(), hitPos, blockPos, side);
			
			IBlockState state = world.getBlockState(blockPos);
			Block block = state.getBlock();
			if(block instanceof IRemoteActivatable) {
				((IRemoteActivatable) block).onRemoteActivated(world, blockPos, state, player, side, 
						(float)(hitPos.x - blockPos.getX()), 
						(float)(hitPos.y - blockPos.getY()),
						(float)(hitPos.z - blockPos.getZ())
						);
			}
		
		}
	}
	
}
