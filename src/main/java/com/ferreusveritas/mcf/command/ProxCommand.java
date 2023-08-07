package com.ferreusveritas.mcf.command;

import com.ferreusveritas.mcf.peripheral.RemoteReceiverPeripheral;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

import java.util.List;

public class ProxCommand {

    public final static String PROX = "prox";

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal(PROX).then(
                Commands.argument("args", ListArgumentType.list())
                        .executes(context -> broadcastProxyEvents(context, ListArgumentType.getList(context, "args")))
        ));
    }

    private static int broadcastProxyEvents(CommandContext<CommandSourceStack> context, List<String> args) throws CommandSyntaxException {
        Entity source = context.getSource().getEntityOrException();
        if (source instanceof Player player) {
            RemoteReceiverPeripheral.broadcastProxyEvents(player, args.toArray(new String[0]));
            return 1;
        }
        return 0;
    }

}