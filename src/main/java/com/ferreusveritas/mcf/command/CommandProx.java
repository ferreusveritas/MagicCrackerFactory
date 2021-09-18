package com.ferreusveritas.mcf.command;

import java.util.List;

import javax.annotation.Nullable;

import com.ferreusveritas.mcf.tileentity.TileRemoteReceiver;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

public class CommandProx extends CommandBase {

	public final static String PROX = "prox";
	
	public String getName() {
		return PROX;
	}

	public int getRequiredPermissionLevel() {
		return 0;
	}

	public String getUsage(ICommandSender sender) {
		return "commands.prox.usage";
	}

	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		
		if(sender instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer)sender;
			
			if(args.length > 0) {
				TileRemoteReceiver.broadcastProxyEvents(player, args);
			}
		}
	}

	/**
	 * Get a list of options for when the user presses the TAB key
	 */
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
		/*if (args.length > 0 && args.length <= 3) {
			return getTabCompletionCoordinate(args, 0, targetPos);
		}
		else if (args.length == 4) {
			return getListOfStringsMatchingLastWord(args, Block.REGISTRY.getKeys());
		}
		else {
			return args.length == 6 ? getListOfStringsMatchingLastWord(args, new String[] {"replace", "destroy", "keep"}) : Collections.emptyList();
		}*/
		
		return super.getTabCompletions(server, sender, args, targetPos);
	}
	
}