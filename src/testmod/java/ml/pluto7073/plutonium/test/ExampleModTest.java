package ml.pluto7073.plutonium.test;

import ml.pluto7073.plutonium.PlutoniumConfig;
import ml.pluto7073.plutonium.config.ServerConfigType;
import net.fabricmc.api.ModInitializer;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ExampleModTest implements ModInitializer {
    public static final Logger LOGGER = LogManager.getLogger("Test Logger");
    public static final ServerConfigType CONFIG_TYPE = new ServerConfigType(ExampleConfig.INSTANCE, ExampleConfig::new);

    @Override
    public void onInitialize() {
        ExampleConfig.INSTANCE.load();
        Registry.register(
                PlutoniumConfig.SERVER_CONFIG_TYPES,
                new ResourceLocation("plutonium_test:server_config"),
                CONFIG_TYPE
        );
    }
}
