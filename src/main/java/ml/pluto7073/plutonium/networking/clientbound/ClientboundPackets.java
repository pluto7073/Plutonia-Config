package ml.pluto7073.plutonium.networking.clientbound;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

public class ClientboundPackets {

    public static void registerPackets() {
        PayloadTypeRegistry.playS2C().register(ClientboundUpdateConfigPacket.TYPE, ClientboundUpdateConfigPacket.STREAM_CODEC);
    }

    @Environment(EnvType.CLIENT)
    public static void init() {
        ClientPlayConnectionEvents.INIT.register((handler, client) -> {

            ClientPlayNetworking.registerGlobalReceiver(ClientboundUpdateConfigPacket.TYPE, (packet, context) -> {
                packet.config().logger.info("Received server config for {}", packet.config().configName);
            });

        });
    }

}
