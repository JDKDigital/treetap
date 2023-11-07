package cy.jdkdigital.treetap.compat.tfc;

import cy.jdkdigital.treetap.common.block.recipe.TapExtractRecipe;
import net.dries007.tfc.util.calendar.Calendars;
import net.dries007.tfc.util.calendar.Month;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraftforge.fml.ModList;

import java.util.Arrays;
import java.util.stream.Collectors;

public class TFCCompat
{
    public static boolean canProcess(TapExtractRecipe recipe) {
        Month month = Calendars.SERVER.getCalendarMonthOfYear();

        return recipe.lifeCycles.length > month.ordinal() && recipe.lifeCycles[month.ordinal()] > 0;
    }

    public static void showRecipeText(GuiGraphics guiGraphics, TapExtractRecipe recipe) {
        if (ModList.get().isLoaded("tfc")) {
            Minecraft minecraft = Minecraft.getInstance();

            var months = Arrays.stream(Month.values()).filter(month -> recipe.lifeCycles[month.ordinal()] > 0).map(Enum::name).toList();

            Component text = Component.translatable("jei.treetap.life_cycle.all_year");
            if (months.size() < 12) {
                text = Component.literal(String.join(", ", months));
            }

            guiGraphics.drawString(minecraft.font, Language.getInstance().getVisualOrder(Component.translatable("jei.treetap.life_cycle.months", text)), 0, 45, 0xFF000000, false);
        }
    }
}
