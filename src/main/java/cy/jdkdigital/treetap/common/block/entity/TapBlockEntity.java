package cy.jdkdigital.treetap.common.block.entity;

import cy.jdkdigital.treetap.TreeTap;
import cy.jdkdigital.treetap.common.block.TapBlock;
import cy.jdkdigital.treetap.common.recipe.TapExtractRecipe;
import cy.jdkdigital.treetap.compat.CompatHandler;
import cy.jdkdigital.treetap.util.ColorUtil;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;

public class TapBlockEntity extends BlockEntity
{
    private int counter = 0;
    public RecipeHolder<TapExtractRecipe> currentRecipe;
    public boolean hasSearchedForRecipe = false;

    public TapBlockEntity(BlockPos pos, BlockState state) {
        super(TreeTap.TAP_BLOCK_ENTITY.get(), pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, TapBlockEntity blockEntity) {
        // Check or lookup recipe
        if (level.getBlockEntity(pos.below()) instanceof SapCollectorBlockEntity sapCollector) {
            if (blockEntity.currentRecipe == null && !blockEntity.hasSearchedForRecipe) {
                var log = level.getBlockState(pos.relative(state.getValue(HorizontalDirectionalBlock.FACING).getOpposite()));
                blockEntity.currentRecipe = TreeTap.getRecipe(level, log);
                sapCollector.setCurrentRecipe(blockEntity.currentRecipe);
                if (blockEntity.currentRecipe != null) {
                    level.setBlockAndUpdate(pos, state.setValue(BlockStateProperties.ATTACHED, true));
                    if (!blockEntity.currentRecipe.value().particleColor.isEmpty()) {
                        TapBlock.color.put(pos, ColorUtil.getCacheColor(ColorUtil.getCacheColor(blockEntity.currentRecipe.value().particleColor)));
                    } else {
                        if (!blockEntity.currentRecipe.value().fluidColor.isEmpty()) {
                            TapBlock.color.put(pos, ColorUtil.getCacheColor(ColorUtil.getCacheColor(blockEntity.currentRecipe.value().fluidColor)));
                        } else {
                            IClientFluidTypeExtensions renderProperties = IClientFluidTypeExtensions.of(blockEntity.currentRecipe.value().displayFluid.getFluid());
                            int fluidTintColour = renderProperties.getTintColor(blockEntity.currentRecipe.value().displayFluid);
                            TapBlock.color.put(pos, ColorUtil.getCacheColor(fluidTintColour));
                        }
                    }
                } else {
                    level.setBlockAndUpdate(pos, state.setValue(BlockStateProperties.ATTACHED, false));
                }
                blockEntity.hasSearchedForRecipe = true;
            } else if (blockEntity.currentRecipe != null) {
                if (CompatHandler.canProcess(blockEntity.currentRecipe)) {

                    int tickRate = TreeTap.recipeTickrate(blockEntity.currentRecipe);
                    if (++blockEntity.counter % tickRate == 0) {
                        if (sapCollector.progress > blockEntity.currentRecipe.value().processingTime) {
                            sapCollector.progress = blockEntity.currentRecipe.value().processingTime;
                        } else {
                            float mod = 1f;
                            if (state.getBlock() instanceof TapBlock tapBlock) {
                                mod = tapBlock.getModifier(level, pos);
                            }

                            sapCollector.addProgress((int)(tickRate * mod));
                            level.sendBlockUpdated(pos.below(), sapCollector.getBlockState(), sapCollector.getBlockState(), Block.UPDATE_CLIENTS);
                        }
                    }
                    if (!state.getValue(BlockStateProperties.ATTACHED)) {
                        level.setBlockAndUpdate(pos, state.setValue(BlockStateProperties.ATTACHED, true));
                    }
                } else if (state.getValue(BlockStateProperties.ATTACHED)) {
                    level.setBlockAndUpdate(pos, state.setValue(BlockStateProperties.ATTACHED, false));
                }
            }
        }
    }

    public void reset() {
        currentRecipe = null;
        hasSearchedForRecipe = false;
    }

    @Override
    protected void loadAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        super.loadAdditional(pTag, pRegistries);
        this.loadPacketNBT(pTag, pRegistries);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider pRegistries) {
        super.saveAdditional(tag, pRegistries);
        this.savePacketNBT(tag, pRegistries);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider pRegistries) {
        return saveWithId(pRegistries);
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt, HolderLookup.Provider lookupProvider) {
        super.onDataPacket(net, pkt, lookupProvider);
        this.loadPacketNBT(pkt.getTag(), lookupProvider);
        if (level instanceof ClientLevel) {
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 0);
        }
    }

    public void loadPacketNBT(CompoundTag tag, HolderLookup.Provider pRegistries) {
        if (tag.contains("recipe") && level != null) {
            var recipe = level.getRecipeManager().byKey(ResourceLocation.parse(tag.getString("recipe")));
            recipe.ifPresent(value -> this.currentRecipe = (RecipeHolder<TapExtractRecipe>) value);
        }
    }

    public void savePacketNBT(CompoundTag tag, HolderLookup.Provider pRegistries) {
        if (this.currentRecipe != null) {
            tag.putString("recipe", this.currentRecipe.id().toString());
        }
    }
}
