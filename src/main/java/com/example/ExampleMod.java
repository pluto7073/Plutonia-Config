package com.example;

import net.fabricmc.api.ModInitializer;

import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExampleMod implements ModInitializer {

    public static final Logger LOGGER = LoggerFactory.getLogger("modid");
	public static final String MOD_ID = "modid";

	@Override
	public void onInitialize() {
		LOGGER.info("Hello Fabric world!");
	}

	public static ResourceLocation id(String id) {
		return new ResourceLocation(MOD_ID, id);
	}

}