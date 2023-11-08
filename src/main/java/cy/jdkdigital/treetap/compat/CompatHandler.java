package cy.jdkdigital.treetap.compat;

import cy.jdkdigital.treetap.common.block.recipe.TapExtractRecipe;
import cy.jdkdigital.treetap.compat.tfc.TFCCompat;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
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
}
