package ml.pluto7073.plutonium.test.client;

import ml.pluto7073.plutonium.annotations.BooleanOption;
import ml.pluto7073.plutonium.annotations.IntOption;
import ml.pluto7073.plutonium.config.JointClientConfig;
import ml.pluto7073.plutonium.test.ExampleModTest;

public class ExampleClientConfig extends JointClientConfig {

    public static final ExampleClientConfig INSTANCE = new ExampleClientConfig();

    @BooleanOption(defaultVal = true) public boolean renderExampleThings;
    @IntOption(defaultVal = 24000) public int tickLengthOfDay;

    private ExampleClientConfig() {
        super("plutonium_test", ExampleModTest.LOGGER, SimpleClientConfig.INSTANCE);
    }

}
