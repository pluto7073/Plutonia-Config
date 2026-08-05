package ml.pluto7073.plutonium;

import ml.pluto7073.plutonium.config.ServerConfigType;
import ml.pluto7073.plutonium.networking.serverbound.ServerboundPackets;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PlutoniumConfig implements ModInitializer {

    public static final Logger LOGGER = LogManager.getLogger("Plutonium");
	public static final String MOD_ID = "plutonium";

	public static final ResourceKey<Registry<ServerConfigType<?>>> SERVER_CONFIG_TYPE_KEY = ResourceKey.createRegistryKey(id("server_config_type"));
	public static final Registry<ServerConfigType<?>> SERVER_CONFIG_TYPES = BuiltInRegistries.registerSimple(SERVER_CONFIG_TYPE_KEY, registry -> ServerConfigType.DEFAULT);

	@Override
	public void onInitialize() {
		LOGGER.info("Configs loaded");;

		ServerboundPackets.registerPackets();
		ServerboundPackets.registerReceivers();
	}

	public static ResourceLocation id(String id) {
		return ResourceLocation.fromNamespaceAndPath(MOD_ID, id);
	}

}