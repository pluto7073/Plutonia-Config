package ml.pluto7073.plutonium.test.config;

import ml.pluto7073.plutonium.annotations.BooleanOption;
import ml.pluto7073.plutonium.config.ServerConfig;
import ml.pluto7073.plutonium.test.ExampleModTest;
import org.apache.logging.log4j.Logger;

public class SimpleConfig2 extends ServerConfig {

    public static SimpleConfig2 INSTANCE_2 = new SimpleConfig2();

    @BooleanOption(hasTooltip = false, defaultVal = true) public boolean booleanOption;

    public SimpleConfig2() {
        super("instance_2", ExampleModTest.LOGGER);
    }

}
