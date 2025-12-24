package ml.pluto7073.plutonium.test;

import ml.pluto7073.plutonium.annotations.*;
import ml.pluto7073.plutonium.config.ServerConfig;

public class ExampleConfig extends ServerConfig {

    public static final ExampleConfig INSTANCE = new ExampleConfig();

    @BooleanOption public boolean exampleBooleanOption;
    @IntOption public int exampleInt;
    @DoubleOption public double exampleDouble;
    @IntOption(max = 10) public int exampleIntSlider;
    @StringOption(defaultVal = "Hello!") public String exampleString;
    @EnumOption("OPT1") public TestEnum exampleEnum;

    public ExampleConfig() {
        super("plutonium_test", ExampleModTest.LOGGER);
    }

    public enum TestEnum {
        OPT1, OPT2, OPT3
    }

}
