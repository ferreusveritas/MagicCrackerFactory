package com.ferreusveritas.mcf.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * @author Harley O'Connor
 */
public interface Message {

    void toBytes(FriendlyByteBuf buffer);

    boolean handle(Supplier<NetworkEvent.Context> context);

    interface Decoder<P extends Message> {
        P fromBytes(FriendlyByteBuf buffer);
    }

}
