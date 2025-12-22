package net.satisfy.earthernware.core.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Tuple;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.satisfy.earthernware.core.block.entity.AbstractStorageBlockEntity;
import net.satisfy.earthernware.core.util.GeneralUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public abstract class AbstractStorageBlock extends AbstractFacingBlock implements EntityBlock {
    public static final BooleanProperty PAINTED = BooleanProperty.create("painted");

    public AbstractStorageBlock(BlockBehaviour.Properties settings) {
        super(settings);
        BlockState baseState = this.stateDefinition.any();
        if (baseState.hasProperty(PAINTED)) {
            this.registerDefaultState(baseState.setValue(PAINTED, false));
        } else {
            this.registerDefaultState(baseState);
        }
    }


    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(PAINTED);
    }

    @Override
    protected @NotNull ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof AbstractStorageBlockEntity storageEntity) {
            Optional<Tuple<Float, Float>> hitCoordinates = GeneralUtil.getRelativeHitCoordinatesForBlockFace(hit, state.getValue(FACING), this.unAllowedDirections());
            if (hitCoordinates.isEmpty()) {
                return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            }

            Tuple<Float, Float> coordinates = hitCoordinates.get();
            int section = this.getSection(coordinates.getA(), coordinates.getB());
            if (section == Integer.MIN_VALUE) {
                return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            }

            if (!storageEntity.getInventory().get(section).isEmpty()) {
                this.remove(world, pos, player, storageEntity, section);
                return ItemInteractionResult.sidedSuccess(world.isClientSide);
            }

            if (!stack.isEmpty() && this.canInsertStack(stack)) {
                this.add(world, pos, player, storageEntity, stack, section);
                return ItemInteractionResult.sidedSuccess(world.isClientSide);
            }

            return ItemInteractionResult.CONSUME;
        }

        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    public void add(Level level, BlockPos pos, Player player, AbstractStorageBlockEntity storageEntity, ItemStack itemStack, int index) {
        if (!level.isClientSide) {
            SoundEvent sound = SoundEvents.GRASS_PLACE;
            storageEntity.setStack(index, itemStack.split(1));
            level.playSound(null, pos, sound, SoundSource.BLOCKS, 1.0F, 1.0F);
            if (player.isCreative()) {
                itemStack.grow(1);
            }

            level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
        }
    }

    public void remove(Level level, BlockPos pos, Player player, AbstractStorageBlockEntity storageEntity, int index) {
        if (!level.isClientSide) {
            ItemStack removedStack = storageEntity.removeStack(index);
            SoundEvent sound = SoundEvents.GRASS_BREAK;
            level.playSound(null, pos, sound, SoundSource.BLOCKS, 1.0F, 1.0F);
            if (!player.getInventory().add(removedStack)) {
                player.drop(removedStack, false);
            }

            level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
        }
    }

    public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean moved) {
        if (!state.is(newState.getBlock())) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof AbstractStorageBlockEntity storageEntity) {
                if (world instanceof ServerLevel serverLevel) {
                    Containers.dropContents(serverLevel, pos, storageEntity.getInventory());
                }

                world.updateNeighbourForOutputSignal(pos, this);
            }

            super.onRemove(state, world, pos, newState, moved);
        }
    }

    public @NotNull RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    public abstract int size();

    public abstract ResourceLocation type();

    public abstract Direction[] unAllowedDirections();

    public abstract boolean canInsertStack(ItemStack stack);

    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new AbstractStorageBlockEntity(pos, state, this.size());
    }

    public abstract int getSection(Float x, Float y);

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        if (level.isClientSide) {
            return;
        }

        int sideColorRgb = 0;
        boolean painted = false;

        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
        if (customData != null) {
            CompoundTag tag = customData.copyTag();

            if (tag.contains("sideColorRgb")) {
                sideColorRgb = tag.getInt("sideColorRgb");
            }

            if (tag.contains("painted")) {
                painted = tag.getBoolean("painted");
            } else {
                painted = sideColorRgb != 0;
            }

            if (state.hasProperty(PAINTED) && state.getValue(PAINTED) != painted) {
                level.setBlock(pos, state.setValue(PAINTED, painted), 2);
                state = level.getBlockState(pos);
            }

            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof AbstractStorageBlockEntity storageEntity) {
                storageEntity.setSideColorRgb(sideColorRgb);
                storageEntity.setPainted(painted);

                if (tag.contains("glazed")) {
                    storageEntity.setGlazed(tag.getBoolean("glazed"));
                }
                if (tag.contains("glazeColorRgb")) {
                    storageEntity.setGlazeColorRgb(tag.getInt("glazeColorRgb"));
                }
                if (tag.contains("glazeStrength")) {
                    storageEntity.setGlazeStrength(tag.getFloat("glazeStrength"));
                }
            }
        }
    }
}