package cy.jdkdigital.treetap.compat.jade;

import cy.jdkdigital.treetap.TreeTap;
import cy.jdkdigital.treetap.common.block.TapBlock;
import cy.jdkdigital.treetap.common.block.entity.TapBlockEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public class TapBlockEntityProvider implements IBlockComponentProvider
{
    public static final ResourceLocation UID = new ResourceLocation(TreeTap.MODID, "tap");

    static final TapBlockEntityProvider INSTANCE = new TapBlockEntityProvider();

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        if (!(accessor.getBlockEntity() instanceof TapBlockEntity blockEntity)) {
            return;
        }

        var modifier = 1.0F;
        if (blockEntity.getBlockState().getBlock() instanceof TapBlock tapBlock) {
            modifier = Math.round(tapBlock.getModifier(accessor.getLevel(), accessor.getPosition()) * 100f) / 100f;
        }

        tooltip.add(Component.translatable("item.treetap.tap.modifier", Component.literal("" + modifier).withStyle(ChatFormatting.GOLD)).withStyle(ChatFormatting.DARK_GREEN));
    }

    @Override
    public ResourceLocation getUid() {
        return UID;
    }
}
