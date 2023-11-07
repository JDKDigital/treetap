package cy.jdkdigital.treetap.compat;

import cy.jdkdigital.treetap.common.block.recipe.TapExtractRecipe;
import cy.jdkdigital.treetap.compat.tfc.TFCCompat;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraftforge.fml.ModList;

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
}
