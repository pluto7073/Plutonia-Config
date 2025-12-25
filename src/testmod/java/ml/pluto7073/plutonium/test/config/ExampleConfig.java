package ml.pluto7073.plutonium.test.config;

import ml.pluto7073.plutonium.annotations.*;
import ml.pluto7073.plutonium.config.JointServerConfig;
import ml.pluto7073.plutonium.test.ExampleModTest;

public class ExampleConfig extends JointServerConfig {

    public static final ExampleConfig INSTANCE = new ExampleConfig();

    @BooleanOption public boolean exampleBooleanOption;
    @IntOption public int exampleInt;
    @DoubleOption public double exampleDouble;
    @IntOption(max = 10) public int exampleIntSlider;
    @StringOption(defaultVal = "Hello!") public String exampleString;
    @EnumOption("OPT1") public TestEnum exampleEnum;

    public ExampleConfig() {
        super("plutonium_test", ExampleModTest.LOGGER, ExampleModTest.SIMPLE_CONFIG_1);
    }

    public enum TestEnum {
        OPT1, OPT2, OPT3
    }

}
