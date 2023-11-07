package cy.jdkdigital.treetap;

import com.mojang.logging.LogUtils;
import cy.jdkdigital.treetap.client.particle.ColoredParticleType;
import cy.jdkdigital.treetap.client.render.block.SapCollectorBlockEntityRenderer;
import cy.jdkdigital.treetap.common.block.SapCollectorBlock;
import cy.jdkdigital.treetap.common.block.TapBlock;
import cy.jdkdigital.treetap.common.block.entity.SapCollectorBlockEntity;
import cy.jdkdigital.treetap.common.block.entity.TapBlockEntity;
import cy.jdkdigital.treetap.common.block.recipe.TapExtractRecipe;
import cy.jdkdigital.treetap.common.item.TapItem;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;

import java.util.List;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(TreeTap.MODID)
public class TreeTap
{
    public static final String MODID = "treetap";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, MODID);
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, MODID);
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(Registries.RECIPE_TYPE, MODID);
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, MODID);

    public static final RegistryObject<RecipeSerializer<?>> TAP_RECIPE_SERIALIZER = RECIPE_SERIALIZERS.register("tap_extract", () -> new TapExtractRecipe.Serializer<>(TapExtractRecipe::new));
    public static final RegistryObject<RecipeType<TapExtractRecipe>> TAP_RECIPE_TYPE = RECIPE_TYPES.register("tap_extract", () -> new RecipeType<>() {});

    public static final RegistryObject<Block> SAP_COLLECTOR = BLOCKS.register("sap_collector", () -> new SapCollectorBlock(BlockBehaviour.Properties.copy(Blocks.CAULDRON)));
    public static final RegistryObject<Block> WOODEN_SAP_COLLECTOR = BLOCKS.register("wooden_sap_collector", () -> new SapCollectorBlock(BlockBehaviour.Properties.copy(Blocks.OAK_WOOD)));
    public static final RegistryObject<BlockEntityType<SapCollectorBlockEntity>> SAP_COLLECTOR_BLOCK_ENTITY = BLOCK_ENTITY.register("sap_collector", () -> BlockEntityType.Builder.of(SapCollectorBlockEntity::new, SAP_COLLECTOR.get(), WOODEN_SAP_COLLECTOR.get()).build(null));
    public static final RegistryObject<Block> TAP = BLOCKS.register("tap", () -> new TapBlock(BlockBehaviour.Properties.copy(Blocks.COPPER_BLOCK).noOcclusion(), 1f));
    public static final RegistryObject<Item> TAP_ITEM = ITEMS.register("tap", () -> new TapItem(TAP.get(), new Item.Properties()));
    public static final RegistryObject<BlockEntityType<TapBlockEntity>> TAP_BLOCK_ENTITY = BLOCK_ENTITY.register("tap", () -> BlockEntityType.Builder.of(TapBlockEntity::new, TAP.get()).build(null));

    public static final RegistryObject<ColoredParticleType> COLORED_DRIP_PARTICLE = PARTICLE_TYPES.register("colored_drip_particle", ColoredParticleType::new);

    public static final TagKey<Block> TAPPABLE = TagKey.create(Registries.BLOCK, new ResourceLocation(MODID, "tappable"));
    public static final TagKey<Item> METAL_BUCKETS = TagKey.create(Registries.ITEM, new ResourceLocation(MODID, "metal_buckets"));
    public static final TagKey<Item> WOODEN_BUCKETS = TagKey.create(Registries.ITEM, new ResourceLocation(MODID, "wooden_buckets"));

    public TreeTap()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        BLOCK_ENTITY.register(modEventBus);
        RECIPE_SERIALIZERS.register(modEventBus);
        RECIPE_TYPES.register(modEventBus);
        PARTICLE_TYPES.register(modEventBus);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

        // Register the item to a creative tab
        modEventBus.addListener(this::addCreative);

        // Register our mod's ForgeConfigSpec so that Forge can create and load the config file for us
//        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
    }

    // Add the example block item to the building blocks tab
    private void addCreative(BuildCreativeModeTabContentsEvent event)
    {
        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            event.accept(TAP_ITEM);
        }
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
        }

        @SubscribeEvent
        public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
            event.registerBlockEntityRenderer(SAP_COLLECTOR_BLOCK_ENTITY.get(), SapCollectorBlockEntityRenderer::new);
        }
    }

    public static TapExtractRecipe getRecipe(Level level, BlockState log) {
        if (log.is(TreeTap.TAPPABLE)) {
            List<TapExtractRecipe> allRecipes = level.getRecipeManager().getAllRecipesFor(TreeTap.TAP_RECIPE_TYPE.get());
            for (var tapExtractRecipe: allRecipes) {
                if (tapExtractRecipe.input.test(new ItemStack(log.getBlock()))) {
                    return tapExtractRecipe;
                }
            }
        }
        return null;
    }

    public static int recipeTickrate(TapExtractRecipe recipe) {
        int mod = recipe.processingTime < 10000 ? 100 : 1000;
        return Math.max(1, recipe.processingTime / mod);
    }
}
