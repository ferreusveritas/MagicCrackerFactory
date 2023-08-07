package com.ferreusveritas.mcf.command;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.synchronization.ArgumentTypes;
import net.minecraft.commands.synchronization.EmptyArgumentSerializer;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.ferreusveritas.mcf.MCF.MOD_ID;

public class ListArgumentType implements ArgumentType<List<String>> {

    private static final ListArgumentType INSTANCE = new ListArgumentType();

    private ListArgumentType() {
    }

    public static ListArgumentType list() {
        return INSTANCE;
    }

    public static void register() {
        ArgumentTypes.register(MOD_ID + ":list", ListArgumentType.class, new EmptyArgumentSerializer<>(ListArgumentType::list));
    }

    @SuppressWarnings("unchecked")
    public static List<String> getList(CommandContext<CommandSourceStack> context, String name) {
        return context.getArgument(name, (Class<List<String>>) (Class<?>) List.class);
    }

    @Override
    public List<String> parse(StringReader reader) {
        String[] elements = reader.getRemaining().split(" ");
        reader.setCursor(reader.getTotalLength());
        return Stream.of(elements).collect(Collectors.toList());
    }
}
