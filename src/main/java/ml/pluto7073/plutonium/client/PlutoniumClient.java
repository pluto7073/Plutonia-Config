package ml.pluto7073.plutonium.client;

import ml.pluto7073.plutonium.networking.clientbound.ClientboundPackets;
import net.fabricmc.api.ClientModInitializer;

public class PlutoniumClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ClientboundPackets.registerPackets();
        ClientboundPackets.init();
    }

}
