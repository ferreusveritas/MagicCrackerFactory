package com.ferreusveritas.mcf;

import net.minecraftforge.common.ForgeConfigSpec;

public class MCFConfigs {

    public static final ForgeConfigSpec SERVER_CONFIG;

    public static final ForgeConfigSpec.IntValue LISTEN_PORT;

    static {
        final ForgeConfigSpec.Builder serverBuilder = new ForgeConfigSpec.Builder();

        serverBuilder.comment("General").push("general");
        LISTEN_PORT = serverBuilder.comment("The port to listen on for incoming requests. Set to `0` to disable.").defineInRange("listenPort", 60000, 0, 0xFFFF);
        serverBuilder.pop();

        SERVER_CONFIG = serverBuilder.build();
    }

}
