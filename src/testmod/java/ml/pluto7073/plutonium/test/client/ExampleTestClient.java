package ml.pluto7073.plutonium.test.client;

import net.fabricmc.api.ClientModInitializer;

public class ExampleTestClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ExampleClientConfig.INSTANCE.load();
    }
}
