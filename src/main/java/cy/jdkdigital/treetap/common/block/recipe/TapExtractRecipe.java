package cy.jdkdigital.treetap.common.block.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import cy.jdkdigital.treetap.TreeTap;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.List;

public class TapExtractRecipe implements Recipe<RecipeInput>
{
    public final Ingredient input;
    public final ItemStack itemOutput;
    public final ItemStack woodenItemOutput;
    public final Ingredient harvestItem;
    public final boolean collectBucket;
    public final int processingTime;
    public final String fluidColor;
    public final FluidStack displayFluid;
    public final List<Integer> lifeCycles;
    public final int requiredBlocks;

    public TapExtractRecipe(Ingredient input, ItemStack itemOutput, ItemStack woodenItemOutput, Ingredient harvestItem, boolean collectBucket, int processingTime, FluidStack displayFluid, String fluidColor, int requiredBlocks, List<Integer> lifeCycles) {
        this.input = input;
        this.itemOutput = itemOutput;
        this.woodenItemOutput = woodenItemOutput;
        this.harvestItem = harvestItem;
        this.collectBucket = collectBucket;
        this.processingTime = processingTime;
        this.fluidColor = fluidColor;
        this.displayFluid = displayFluid;
        this.requiredBlocks = requiredBlocks;
        this.lifeCycles = lifeCycles;
    }

    @Override
    public boolean matches(RecipeInput container, Level level) {
        return false;
    }

    @Override
    public @NotNull ItemStack assemble(RecipeInput container, HolderLookup.Provider pRegistries) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int w, int h) {
        return false;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider pRegistries) {
        return ItemStack.EMPTY;
    }

    public ItemStack getResultItem(BlockState blockState) {
        boolean woodResult = blockState.is(TreeTap.WOODEN_SAP_COLLECTOR.get());
        return woodResult ? woodenItemOutput.copy() : itemOutput.copy();
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return TreeTap.TAP_RECIPE_SERIALIZER.get();
    }

    @Override
    public @NotNull RecipeType<?> getType() {
        return TreeTap.TAP_RECIPE_TYPE.get();
    }

    public static class Serializer implements RecipeSerializer<TapExtractRecipe>
    {
        private static final MapCodec<TapExtractRecipe> CODEC = RecordCodecBuilder.mapCodec(
                builder -> builder.group(
                                Ingredient.CODEC.fieldOf("log").forGetter(recipe -> recipe.input),
                                ItemStack.CODEC.fieldOf("result").forGetter(recipe -> recipe.itemOutput),
                                ItemStack.CODEC.fieldOf("wooden_result").orElse(ItemStack.EMPTY).forGetter(recipe -> recipe.woodenItemOutput),
                                Ingredient.CODEC.fieldOf("harvest_item").orElse(Ingredient.EMPTY).forGetter(recipe -> recipe.harvestItem),
                                Codec.BOOL.fieldOf("collect_bucket").orElse(false).forGetter(recipe -> recipe.collectBucket),
                                Codec.INT.fieldOf("processing_time").orElse(1000).forGetter(recipe -> recipe.processingTime),
                                FluidStack.CODEC.fieldOf("display_fluid").orElse(new FluidStack(Fluids.WATER, 1000)).forGetter(recipe -> recipe.displayFluid),
                                Codec.STRING.fieldOf("fluid_color").orElse("").forGetter(recipe -> recipe.fluidColor),
                                Codec.INT.fieldOf("required_block_count").orElse(1).forGetter(recipe -> recipe.requiredBlocks),
                                Codec.INT.listOf().fieldOf("life_cycle").orElse(List.of(1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1)).forGetter(recipe -> recipe.lifeCycles)
                        )
                        .apply(builder, TapExtractRecipe::new)
        );

        public static final StreamCodec<RegistryFriendlyByteBuf, TapExtractRecipe> STREAM_CODEC = StreamCodec.of(
                TapExtractRecipe.Serializer::toNetwork, TapExtractRecipe.Serializer::fromNetwork
        );

        @Override
        public MapCodec<TapExtractRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, TapExtractRecipe> streamCodec() {
            return STREAM_CODEC;
        }

        public static TapExtractRecipe fromNetwork(@Nonnull RegistryFriendlyByteBuf buffer) {
            try {
                return new TapExtractRecipe(
                        Ingredient.CONTENTS_STREAM_CODEC.decode(buffer),
                        ItemStack.STREAM_CODEC.decode(buffer),
//                        ItemStack.STREAM_CODEC.decode(buffer),
                        ItemStack.EMPTY,
                        Ingredient.CONTENTS_STREAM_CODEC.decode(buffer),
                        buffer.readBoolean(),
                        buffer.readInt(),
                        FluidStack.STREAM_CODEC.decode(buffer),
                        buffer.readUtf(),
                        buffer.readInt(),
                        buffer.readList(FriendlyByteBuf::readInt)
                );
            } catch (Exception e) {
                TreeTap.LOGGER.error("Error reading tap extract recipe from packet.", e);
                throw e;
            }
        }

        public static void toNetwork(@Nonnull RegistryFriendlyByteBuf buffer, TapExtractRecipe recipe) {
            try {
                Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.input);
                ItemStack.STREAM_CODEC.encode(buffer, recipe.itemOutput);
//                ItemStack.STREAM_CODEC.encode(buffer, recipe.woodenItemOutput);
                Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, recipe.harvestItem);
                buffer.writeBoolean(recipe.collectBucket);
                buffer.writeInt(recipe.processingTime);
                FluidStack.STREAM_CODEC.encode(buffer, recipe.displayFluid);
                buffer.writeUtf(recipe.fluidColor);
                buffer.writeInt(recipe.requiredBlocks);
                buffer.writeCollection(recipe.lifeCycles, FriendlyByteBuf::writeInt);
            } catch (Exception e) {
                TreeTap.LOGGER.error("Error writing tap extract recipe to packet.", e);
                throw e;
            }
        }
    }
}
