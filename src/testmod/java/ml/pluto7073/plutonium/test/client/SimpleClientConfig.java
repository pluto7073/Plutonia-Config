package ml.pluto7073.plutonium.test.client;

import ml.pluto7073.plutonium.annotations.DoubleOption;
import ml.pluto7073.plutonium.config.ClientConfig;
import ml.pluto7073.plutonium.test.ExampleModTest;
import org.apache.logging.log4j.Logger;

public class SimpleClientConfig extends ClientConfig {

    public static final SimpleClientConfig INSTANCE = new SimpleClientConfig();

    @DoubleOption(defaultVal = 1.0, hasTooltip = false) public double managedFromInstance;

    public SimpleClientConfig() {
        super("instance_1", ExampleModTest.LOGGER, true);
    }

}
