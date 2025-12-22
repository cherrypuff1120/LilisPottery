package net.satisfy.earthernware;

import net.minecraft.resources.ResourceLocation;
import net.satisfy.earthernware.core.event.UrnDeathHandler;
import net.satisfy.earthernware.core.registry.*;

public class Earthernware {
    public static final String MOD_ID = "earthernware";

    public static ResourceLocation identifier(String name) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, name);
    }

    public static void init() {
        ObjectRegistry.init();
        EntityTypeRegistry.init();
        TabRegistry.init();
        RecipeRegistry.init();
        ScreenHandlerRegistry.init();
        UrnDeathHandler.init();
    }

    public static void commonInit() {
        FlammableBlockRegistry.init();
    }
}