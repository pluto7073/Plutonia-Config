package ml.pluto7073.plutonium.test;

import ml.pluto7073.plutonium.PlutoniumConfig;
import ml.pluto7073.plutonium.config.ServerConfigType;
import ml.pluto7073.plutonium.test.config.ExampleConfig;
import ml.pluto7073.plutonium.test.config.SimpleConfig;
import ml.pluto7073.plutonium.test.config.SimpleConfig2;
import net.fabricmc.api.ModInitializer;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ExampleModTest implements ModInitializer {
    public static final Logger LOGGER = LogManager.getLogger("Test Logger");

    public static final ServerConfigType<SimpleConfig> SIMPLE_CONFIG_1 = new ServerConfigType<>(SimpleConfig.INSTANCE_1, SimpleConfig::new, true);
    public static final ServerConfigType<SimpleConfig2> SIMPLE_CONFIG_2 = new ServerConfigType<>(SimpleConfig2.INSTANCE_2, SimpleConfig2::new, true);

    public static final ServerConfigType<ExampleConfig> CONFIG_TYPE = new ServerConfigType<>(ExampleConfig.INSTANCE, ExampleConfig::new);

    @Override
    public void onInitialize() {
        Registry.register(PlutoniumConfig.SERVER_CONFIG_TYPES, id("server_config"), CONFIG_TYPE);
        Registry.register(PlutoniumConfig.SERVER_CONFIG_TYPES, id("instance_1"), SIMPLE_CONFIG_1);
        Registry.register(PlutoniumConfig.SERVER_CONFIG_TYPES, id("instance_2"), SIMPLE_CONFIG_2);
        ExampleConfig.INSTANCE.addManagedConfig(SIMPLE_CONFIG_2);
        ExampleConfig.INSTANCE.load();
    }

    public static ResourceLocation id(String name) {
        return ResourceLocation.fromNamespaceAndPath("plutonium_test", name);
    }
}
