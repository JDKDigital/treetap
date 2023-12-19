package cy.jdkdigital.treetap.compat.tfc;

import cy.jdkdigital.treetap.TreeTap;
import cy.jdkdigital.treetap.common.block.TapBlock;
import cy.jdkdigital.treetap.common.block.recipe.TapExtractRecipe;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.TFCBlockStateProperties;
import net.dries007.tfc.common.blocks.wood.BranchDirection;
import net.dries007.tfc.util.AxeLoggingHelper;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.calendar.Calendars;
import net.dries007.tfc.util.calendar.Month;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.fml.ModList;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class TFCCompat
{
    public static boolean canProcess(TapExtractRecipe recipe) {
        Month month = Calendars.SERVER.getCalendarMonthOfYear();

        return recipe.lifeCycles.length > month.ordinal() && recipe.lifeCycles[month.ordinal()] > 0;
    }

    public static void showRecipeText(GuiGraphics guiGraphics, TapExtractRecipe recipe) {
        if (ModList.get().isLoaded("tfc")) {
            Minecraft minecraft = Minecraft.getInstance();

            var months = Arrays.stream(Month.values()).filter(month -> recipe.lifeCycles[month.ordinal()] > 0).map(e -> capitalize(e.name())).toList();

            Component text = Component.translatable("jei.treetap.life_cycle.all_year");
            if (months.size() < 12) {
                text = Component.literal(String.join(", ", months));
            }

            guiGraphics.drawString(minecraft.font, Language.getInstance().getVisualOrder(Component.translatable("jei.treetap.life_cycle.months", text)), 0, 60, 0xFF000000, false);
        }
    }

    private static String capitalize(String name) {
        String[] nameParts = name.toLowerCase().split("_");

        for (int i = 0; i < nameParts.length; i++) {
            nameParts[i] = nameParts[i].substring(0, 1).toUpperCase() + nameParts[i].substring(1);
        }

        return String.join(" ", nameParts);
    }

    public static void appendHoverText(List<Component> pTooltip, Block block) {
        float mod = 1.0f;
        if (block instanceof TapBlock tapBlock) {
            mod = tapBlock.getModifier(null, null);
        }
        pTooltip.add(Component.translatable("item.treetap.tap.modifier", Component.literal("" + mod).withStyle(ChatFormatting.GOLD)).withStyle(ChatFormatting.DARK_GREEN));
    }

    public static boolean isValidTree(LevelReader level, BlockPos pos) {
        var branchState = level.getBlockState(pos);
        return !Helpers.isBlock(branchState.getBlock(), TFCTags.Blocks.LOGS_THAT_LOG) || (branchState.hasProperty(TFCBlockStateProperties.BRANCH_DIRECTION) && branchState.getValue(TFCBlockStateProperties.BRANCH_DIRECTION).equals(BranchDirection.DOWN));
    }

    public static float adjustTapModifier(Level level, BlockPos pos, float modifier) {
        // Check for other taps on same trunk
        AtomicInteger taps = new AtomicInteger(0);
        while (level.getBlockState(pos.below()).is(TreeTap.TAPPABLE)) {
            pos = pos.below();
        }
        AxeLoggingHelper.findLogs(level, pos).forEach(blockPos -> {
            for (Direction direction : Direction.values()) {
                var sideState = level.getBlockState(blockPos.relative(direction));
                if (sideState.is(TreeTap.TAP.get()) && sideState.hasProperty(BlockStateProperties.ATTACHED) && sideState.getValue(BlockStateProperties.ATTACHED)) {
                    taps.getAndIncrement();
                }
            }
        });
        return modifier / Math.max(1, taps.get());
    }
}
