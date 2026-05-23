package cy.jdkdigital.treetap;

import com.mojang.logging.LogUtils;
import cy.jdkdigital.treetap.client.particle.ColoredParticleType;
import cy.jdkdigital.treetap.common.block.SapCollectorBlock;
import cy.jdkdigital.treetap.common.block.TapBlock;
import cy.jdkdigital.treetap.common.block.entity.SapCollectorBlockEntity;
import cy.jdkdigital.treetap.common.block.entity.TapBlockEntity;
import cy.jdkdigital.treetap.common.recipe.TapExtractRecipe;
import cy.jdkdigital.treetap.common.item.TapItem;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.slf4j.Logger;

import java.util.List;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(TreeTap.MODID)
public class TreeTap
{
    public static final String MODID = "treetap";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(BuiltInRegistries.BLOCK, MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(BuiltInRegistries.ITEM, MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, MODID);
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(BuiltInRegistries.RECIPE_SERIALIZER, MODID);
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(Registries.RECIPE_TYPE, MODID);
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(BuiltInRegistries.PARTICLE_TYPE, MODID);

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<?>> TAP_RECIPE_SERIALIZER = RECIPE_SERIALIZERS.register("tap_extract", TapExtractRecipe.Serializer::new);
    public static final DeferredHolder<RecipeType<?>, RecipeType<TapExtractRecipe>> TAP_RECIPE_TYPE = RECIPE_TYPES.register("tap_extract", () -> new RecipeType<>() {});

    public static final DeferredHolder<Block, Block> SAP_COLLECTOR = BLOCKS.register("sap_collector", () -> new SapCollectorBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.CAULDRON)));
    public static final DeferredHolder<Block, Block> WOODEN_SAP_COLLECTOR = BLOCKS.register("wooden_sap_collector", () -> new SapCollectorBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_WOOD)));
    public static final DeferredHolder<Block, Block> CERAMIC_SAP_COLLECTOR = BLOCKS.register("ceramic_sap_collector", () -> new SapCollectorBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.TERRACOTTA)));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SapCollectorBlockEntity>> SAP_COLLECTOR_BLOCK_ENTITY = BLOCK_ENTITY.register("sap_collector", () -> BlockEntityType.Builder.of(SapCollectorBlockEntity::new, SAP_COLLECTOR.get(), WOODEN_SAP_COLLECTOR.get(), CERAMIC_SAP_COLLECTOR.get()).build(null));
    public static final DeferredHolder<Block, Block> TAP = BLOCKS.register("tap", () -> new TapBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.COPPER_BLOCK).noOcclusion(), 1f));
    public static final DeferredHolder<Item, Item> TAP_ITEM = ITEMS.register("tap", () -> new TapItem(TAP.get(), new Item.Properties()));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TapBlockEntity>> TAP_BLOCK_ENTITY = BLOCK_ENTITY.register("tap", () -> BlockEntityType.Builder.of(TapBlockEntity::new, TAP.get()).build(null));

    public static final DeferredHolder<ParticleType<?>, ColoredParticleType> COLORED_DRIP_PARTICLE = PARTICLE_TYPES.register("colored_drip_particle", ColoredParticleType::new);

    public static final TagKey<Block> TAPPABLE = TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(MODID, "tappable"));
    public static final TagKey<Item> TAPS = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(MODID, "taps"));
    public static final TagKey<Item> METAL_BUCKETS = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(MODID, "metal_buckets"));
    public static final TagKey<Item> WOODEN_BUCKETS = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(MODID, "wooden_buckets"));
    public static final TagKey<Item> CERAMIC_BUCKETS = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(MODID, "ceramic_buckets"));

    public TreeTap(IEventBus modEventBus, ModContainer modContainer)
    {
        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        BLOCK_ENTITY.register(modEventBus);
        RECIPE_SERIALIZERS.register(modEventBus);
        RECIPE_TYPES.register(modEventBus);
        PARTICLE_TYPES.register(modEventBus);
    }


    public static RecipeHolder<TapExtractRecipe> getRecipe(Level level, BlockState log) {
        if (log.is(TreeTap.TAPPABLE)) {
            List<RecipeHolder<TapExtractRecipe>> allRecipes = level.getRecipeManager().getAllRecipesFor(TreeTap.TAP_RECIPE_TYPE.get());
            for (var tapExtractRecipe: allRecipes) {
                if (tapExtractRecipe.value().input.test(new ItemStack(log.getBlock()))) {
                    return tapExtractRecipe;
                }
            }
        }
        return null;
    }

    public static int recipeTickrate(RecipeHolder<TapExtractRecipe> recipe) {
        int mod = recipe.value().processingTime < 10000 ? 100 : 1000;
        return Math.max(1, recipe.value().processingTime / mod);
    }
}
