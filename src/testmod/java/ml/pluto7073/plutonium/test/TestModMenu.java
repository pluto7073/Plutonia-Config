package ml.pluto7073.plutonium.test;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import ml.pluto7073.plutonium.client.gui.PlutoniumConfigScreen;
import ml.pluto7073.plutonium.test.client.ExampleClientConfig;

public class TestModMenu extends PlutoniumConfigScreen implements ModMenuApi {

    public TestModMenu() {
        super(ExampleClientConfig.INSTANCE, ExampleModTest.CONFIG_TYPE, "plutonium_test");
    }

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return this::apply;
    }

}
