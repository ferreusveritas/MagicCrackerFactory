package com.ferreusveritas.mcf.network;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * @author Harley O'Connor
 */
public interface Message {

    void toBytes(PacketBuffer buffer);

    boolean handle(Supplier<NetworkEvent.Context> context);

    interface Decoder<P extends Message> {
        P fromBytes(PacketBuffer buffer);
    }

}
