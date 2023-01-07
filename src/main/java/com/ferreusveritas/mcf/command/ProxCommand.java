package com.ferreusveritas.mcf.command;

import com.ferreusveritas.mcf.peripheral.RemoteReceiverPeripheral;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.apache.logging.log4j.LogManager;

import java.util.List;

public class ProxCommand {

    public final static String PROX = "prox";

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal(PROX).then(
                Commands.argument("args", ListArgumentType.list())
                        .executes(context -> broadcastProxyEvents(context, ListArgumentType.getList(context, "args")))
        ));
    }

    private static int broadcastProxyEvents(CommandContext<CommandSource> context, List<String> args) throws CommandSyntaxException {
        Entity source = context.getSource().getEntityOrException();
        if (source instanceof PlayerEntity) {
            RemoteReceiverPeripheral.broadcastProxyEvents(((PlayerEntity) source), args.toArray(new String[0]));
            return 1;
        }
        return 0;
    }

}