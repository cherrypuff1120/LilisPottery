package net.satisfy.earthernware.fabric.client;

import net.fabricmc.api.ClientModInitializer;
import net.satisfy.earthernware.client.EarthernwareClient;

public class EarthernwareClientFabric implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EarthernwareClient.onInitializeClient();
        EarthernwareClient.preInitClient();
    }
}
