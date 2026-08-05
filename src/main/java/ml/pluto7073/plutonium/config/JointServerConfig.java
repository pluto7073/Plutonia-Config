package ml.pluto7073.plutonium.config;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import ml.pluto7073.plutonium.PlutoniumConfig;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JointServerConfig extends ServerConfig {

    protected final List<ServerConfigType<?>> managedConfigs;

    public JointServerConfig(String modid, Logger logger, ServerConfigType<?> type, boolean copy, ServerConfigType<?>... managedConfigs) {
        super(modid, logger, type, copy);
        for (ServerConfigType<?> managedConfig : managedConfigs) {
            if (!managedConfig.isManaged()) {
                throw new IllegalArgumentException("All configs added to a JointConfig must be managed, " + managedConfig.serverConfig.configName + " is not");
            }
        }
        this.managedConfigs = new ArrayList<>(List.of(managedConfigs));
    }

    public void addManagedConfig(ServerConfigType<?>... managedConfigs) {
        for (ServerConfigType<?> managedConfig : managedConfigs) {
            if (!managedConfig.isManaged()) {
                throw new IllegalArgumentException("All configs added to a JointConfig must be managed, " + managedConfig.serverConfig.configName + " is not");
            }
        }

        this.managedConfigs.addAll(List.of(managedConfigs));
    }

    @Override
    public void save() {
        super.save();

        for (ServerConfigType<?> managedConfig : managedConfigs) {
            if (!copy) {
                managedConfig.serverConfig.save();
            }
        }
    }

    @Override
    public void load() {
        super.load();

        for (ServerConfigType<?> managedConfig : managedConfigs) {
            if (!copy) {
                managedConfig.serverConfig.load();
            }
        }
    }

    @Override
    public void writeToPacket(FriendlyByteBuf buf) {
        super.writeToPacket(buf); // Write this config
        for (ServerConfigType<?> managedConfig : managedConfigs) {
            // Loop through managed configs
            ResourceLocation key = PlutoniumConfig.SERVER_CONFIG_TYPES.getKey(managedConfig);
            if (key == null) {
                logger.warn("Managed config {} is unregistered, can't sync!", managedConfig.serverConfig.configName);
                break;
            }
            buf.writeResourceLocation(key);
            if (copy) {
                managedConfig.getCopy().writeToPacket(buf);
            } else {
                managedConfig.serverConfig.writeToPacket(buf);
            }
        }

        buf.writeResourceLocation(ResourceLocation.withDefaultNamespace("empty"));
    }

    @Override
    public void loadFromPacket(FriendlyByteBuf buf) {
        super.loadFromPacket(buf); // Load this config
        try {
            while (true) {
                // Loop until buf is read in full or end id reached
                ResourceLocation id = buf.readResourceLocation();
                if ("minecraft:empty".equals(id.toString())) break;
                ServerConfigType<?> type = PlutoniumConfig.SERVER_CONFIG_TYPES.get(id);
                if (type == null || !type.isManaged()) throw new IllegalStateException(); // This shouldn't happen but just in case
                if (copy) {
                    type.updateCopy(buf);
                } else {
                    type.updateOriginal(buf);
                }
                if (!managedConfigs.contains(type) && copy) { // Configs added later should be added to the copy if they don't exist as wel as existing client configs
                    managedConfigs.add(type);
                }
            }
        } catch (IndexOutOfBoundsException ignored) {} // If end is reached then assume configs loaded and empty id forgot
    }

    @Override
    public Map<String, OptionInstance> getFields() {
        HashMap<String, OptionInstance> allFields = new HashMap<>(super.getFields());

        for (ServerConfigType<?> config : managedConfigs) {
            if (copy) {
                allFields.putAll(config.getCopy().fields);
            } else {
                allFields.putAll(config.serverConfig.fields);
            }
        }
        return ImmutableMap.copyOf(allFields);
    }
}
