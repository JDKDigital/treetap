package cy.jdkdigital.treetap.compat;

import cy.jdkdigital.treetap.TreeTap;
import cy.jdkdigital.treetap.common.block.recipe.TapExtractRecipe;
import cy.jdkdigital.treetap.compat.tfc.TFCCompat;
import cy.jdkdigital.treetap.compat.dynamictrees.DTCompat;
import cy.jdkdigital.treetap.util.TreeUtil;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
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

    public static boolean isValidTree(LevelReader levelReader, BlockPos pos) {
        //prioritize Dynamic Trees Valid tree registration over TFC
        if (ModList.get().isLoaded("dynamictrees")) {
            return DTCompat.isValidTree(levelReader, pos);
        }
        else if (ModList.get().isLoaded("tfc")) {
            return TFCCompat.isValidTree(levelReader, pos);
        }
        if (levelReader instanceof Level level) {
            var recipe = TreeTap.getRecipe(level, level.getBlockState(pos));
            if (recipe != null && recipe.requiredBlocks > 1) {
                return TreeUtil.isValidTree(level, pos, recipe.requiredBlocks);
            }
        }
        return true;
    }

    public static float adjustTapModifier(Level level, BlockPos pos, float modifier) {
        if (ModList.get().isLoaded("tfc")) {
            return TFCCompat.adjustTapModifier(level, pos, modifier);
        }
        return TreeUtil.adjustTapModifier(level, pos, modifier);
    }
}
