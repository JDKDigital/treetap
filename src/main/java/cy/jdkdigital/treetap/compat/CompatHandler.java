package cy.jdkdigital.treetap.compat;

import cy.jdkdigital.treetap.common.block.recipe.TapExtractRecipe;
import cy.jdkdigital.treetap.compat.tfc.TFCCompat;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fml.ModList;

import java.util.List;

public class CompatHandler
{
    public static boolean canProcess(TapExtractRecipe recipe) {
        if (ModList.get().isLoaded("tfc")) {
            return TFCCompat.canProcess(recipe);
        }
        return true;
    }

    public static void showRecipeText(GuiGraphics guiGraphics, TapExtractRecipe recipe) {
        if (ModList.get().isLoaded("tfc")) {
            TFCCompat.showRecipeText(guiGraphics, recipe);
        }
    }

    public static void appendHoverText(List<Component> pTooltip, Block block) {
        if (ModList.get().isLoaded("tfc")) {
            TFCCompat.appendHoverText(pTooltip, block);
        }
    }

    public static boolean isValidTree(LevelReader level, BlockPos pos) {
        if (ModList.get().isLoaded("tfc")) {
            return TFCCompat.isValidTree(level, pos);
        }
        return true;
    }

    public static float adjustTapModifier(Level level, BlockPos pos, float modifier) {
        if (ModList.get().isLoaded("tfc")) {
            return TFCCompat.adjustTapModifier(level, pos, modifier);
        }
        return modifier;
    }
}
