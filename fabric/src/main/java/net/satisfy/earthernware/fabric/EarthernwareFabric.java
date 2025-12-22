package net.satisfy.earthernware.fabric;

import net.fabricmc.api.ModInitializer;
import net.satisfy.earthernware.Earthernware;
import net.satisfy.earthernware.fabric.core.world.EarthernwareFabricWorldgen;

public class EarthernwareFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        Earthernware.init();
        EarthernwareFabricWorldgen.init();
    }
}
