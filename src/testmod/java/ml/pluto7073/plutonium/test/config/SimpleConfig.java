package ml.pluto7073.plutonium.test.config;

import ml.pluto7073.plutonium.annotations.StringOption;
import ml.pluto7073.plutonium.config.ServerConfig;
import ml.pluto7073.plutonium.config.ServerConfigType;
import ml.pluto7073.plutonium.test.ExampleModTest;
import org.apache.logging.log4j.Logger;

public class SimpleConfig extends ServerConfig {

    public static final SimpleConfig INSTANCE_1 = new SimpleConfig(ExampleModTest.SIMPLE_CONFIG_1, false);

    @StringOption(defaultVal = "test") public String configOption1;

    public SimpleConfig(ServerConfigType<?> type, boolean copy) {
        super("instance_1", ExampleModTest.LOGGER, type, copy);
    }

}
