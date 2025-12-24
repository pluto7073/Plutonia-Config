package ml.pluto7073.plutonium.networking.clientbound;

import ml.pluto7073.plutonium.PlutoniumConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketType;

public class ClientboundPackets {

    public static final PacketType<ClientboundUpdateConfigPacket> UPDATE_CONFIG =
            PacketType.create(PlutoniumConfig.id("clientbound/update_config"), ClientboundUpdateConfigPacket::read);

    @Environment(EnvType.CLIENT)
    public static void init() {
        ClientPlayConnectionEvents.INIT.register((handler, client) -> {

            ClientPlayNetworking.registerGlobalReceiver(UPDATE_CONFIG, (packet, player, sender) -> {
                packet.config().logger.info("Received server config for {}", packet.config().configName);
            });

        });
    }

}
