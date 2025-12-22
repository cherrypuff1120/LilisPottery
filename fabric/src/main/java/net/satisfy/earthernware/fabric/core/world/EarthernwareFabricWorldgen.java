package net.satisfy.earthernware.fabric.core.world;

import net.fabricmc.fabric.api.biome.v1.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.satisfy.earthernware.Earthernware;

import java.util.function.Predicate;

public final class EarthernwareFabricWorldgen {

    public static void init() {
        registerFeatureAdditions();
    }

    static Predicate<BiomeSelectionContext> grove = BiomeSelectors.includeByKey(Biomes.GROVE);

    public static final ResourceKey<PlacedFeature> ALPINE_GNEISS_BOULDER_PLACED = registerPlacedFeature("alpine_gneiss_boulder_placed");

    public static void registerFeatureAdditions() {
        BiomeModification world = BiomeModifications.create(ResourceLocation.fromNamespaceAndPath(Earthernware.MOD_ID, "world_features"));

        //world.add(ModificationPhase.ADDITIONS, grove, context -> context.getGenerationSettings().addFeature(GenerationStep.Decoration.TOP_LAYER_MODIFICATION, SNOW_UNDER_TREES_PLACED));

    }

    public static ResourceKey<PlacedFeature> registerPlacedFeature(String name) {
        return ResourceKey.create(Registries.PLACED_FEATURE, Earthernware.identifier(name));
    }
}