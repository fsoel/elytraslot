package com.dermememann.elytra;

import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Elytra implements ModInitializer {
	public static final String MOD_ID = "elytra";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("Initialized Elytra mod (server)");
	}
}