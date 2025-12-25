package ml.pluto7073.plutonium.config;

import com.google.common.collect.ImmutableMap;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JointClientConfig extends ClientConfig {

    private final List<ClientConfig> managedConfigs;

    public JointClientConfig(String modid, Logger logger, ClientConfig... managedConfigs) {
        super(modid, logger);
        this.managedConfigs = new ArrayList<>();
        for (ClientConfig managedConfig : managedConfigs) { // Certify that all configs are manageable
            if (!managedConfig.isManaged()) {
                throw new IllegalArgumentException("ClientConfig " + managedConfig.configName + " cannot be added to a JointConfig because it isn't managed");
            }
        }
        this.managedConfigs.addAll(List.of(managedConfigs));
    }

    public void addManagedConfig(ClientConfig config) {
        if (!config.isManaged()) {
            throw new IllegalArgumentException("ClientConfig " + config.configName + " cannot be added to JointConfig as it is not managed");
        }
        this.managedConfigs.add(config);
    }

    @Override
    public void save() {
        super.save();

        managedConfigs.forEach(ClientConfig::save);
    }

    @Override
    public void load() {
        super.load();

        managedConfigs.forEach(ClientConfig::load);
    }

    @Override
    public Map<String, OptionInstance> getFields() {
        HashMap<String, OptionInstance> allFields = new HashMap<>(this.fields);
        for (ClientConfig config : managedConfigs) {
            allFields.putAll(config.fields);
        }
        return ImmutableMap.copyOf(allFields);
    }
}
