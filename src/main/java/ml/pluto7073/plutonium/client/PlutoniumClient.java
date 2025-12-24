package ml.pluto7073.plutonium.client;

import ml.pluto7073.plutonium.annotations.IntOption;
import ml.pluto7073.plutonium.networking.clientbound.ClientboundPackets;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.Minecraft;

public class PlutoniumClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ClientboundPackets.init();
    }

}
