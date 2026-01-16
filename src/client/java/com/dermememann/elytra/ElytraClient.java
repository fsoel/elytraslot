package com.dermememann.elytra;

import net.fabricmc.api.ClientModInitializer;

public class ElytraClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ChestplateEquip.register();
	}
}