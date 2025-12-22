package net.satisfy.earthernware.client.util;

import dev.architectury.registry.client.rendering.ColorHandlerRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.satisfy.earthernware.core.block.AbstractStorageBlock;
import net.satisfy.earthernware.core.block.entity.AbstractStorageBlockEntity;

import java.util.ArrayList;
import java.util.List;

public final class StoragePaintTintUtil {

    private StoragePaintTintUtil() {
    }

    public static void register() {
        List<Block> storageBlocks = new ArrayList<>();
        List<Item> storageItems = new ArrayList<>();

        for (Block block : BuiltInRegistries.BLOCK) {
            if (block instanceof AbstractStorageBlock) {
                storageBlocks.add(block);
                storageItems.add(block.asItem());
            }
        }

        ColorHandlerRegistry.registerBlockColors(StoragePaintTintUtil::blockTint, storageBlocks.toArray(Block[]::new));
        ColorHandlerRegistry.registerItemColors(StoragePaintTintUtil::itemTint, storageItems.toArray(Item[]::new));
    }

    private static int blockTint(BlockState state, BlockAndTintGetter level, BlockPos pos, int tintIndex) {
        if (tintIndex != 0) {
            return -1;
        }
        if (!state.hasProperty(AbstractStorageBlock.PAINTED) || !state.getValue(AbstractStorageBlock.PAINTED)) {
            return -1;
        }
        if (level == null || pos == null) {
            return -1;
        }

        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof AbstractStorageBlockEntity storageEntity) {
            int rgb = storageEntity.getSideColorRgb();
            return rgb == 0 ? -1 : rgb;
        }
        return -1;
    }

    private static int itemTint(ItemStack stack, int tintIndex) {
        if (tintIndex != 0) {
            return -1;
        }

        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
        if (customData == null) {
            return -1;
        }

        CompoundTag tag = customData.copyTag();

        boolean painted = tag.contains("painted") ? tag.getBoolean("painted") : (tag.contains("sideColorRgb") && tag.getInt("sideColorRgb") != 0);
        if (!painted) {
            return -1;
        }

        int rgb = tag.contains("sideColorRgb") ? tag.getInt("sideColorRgb") : 0;
        return rgb == 0 ? -1 : rgb;
    }
}
