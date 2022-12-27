package com.ferreusveritas.mcf.network;

import com.ferreusveritas.mcf.MCF;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class Networking {

    private static SimpleChannel channel;
    private static int id;

    public static void createChannel() {
        channel = NetworkRegistry.newSimpleChannel(
                MCF.location(MCF.MOD_ID), () -> "1.0", s -> true, s -> true
        );
    }

    public static void registerPackets() {
        registerPacket(ServerBoundRemoteClickMessage.class, ServerBoundRemoteClickMessage.DECODER);
        registerPacket(ServerBoundTouchMapMessage.class, ServerBoundTouchMapMessage.DECODER);
    }

    private static <P extends Message> void registerPacket(Class<P> messageClass, Message.Decoder<P> decoder) {
        channel.messageBuilder(messageClass, id++)
                .encoder(Message::toBytes)
                .decoder(decoder::fromBytes)
                .consumer(Message::handle)
                .add();
    }

    @OnlyIn(Dist.CLIENT)
    public static void sendToServer(Message message) {
        channel.sendToServer(message);
    }

}
