package net.satisfy.earthernware.fabric.datagen;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.satisfy.earthernware.fabric.datagen.lang.EWEnglishLangGen;
import net.satisfy.earthernware.fabric.datagen.model.EWModelGen;
import net.satisfy.earthernware.fabric.datagen.model.PotteryModelGen;

public class EarthernwareDatagen implements DataGeneratorEntrypoint {
    static FabricDataGenerator.Pack mainPack;

    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        mainPack = fabricDataGenerator.createPack();

        mainPack.addProvider(EWEnglishLangGen::new);
        mainPack.addProvider(EWModelGen::new);
    }
}
