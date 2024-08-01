package cy.jdkdigital.treetap.util;

import cy.jdkdigital.treetap.common.block.entity.SapCollectorBlockEntity;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;

public class ProgressFluidTank extends FluidTank
{
    private final SapCollectorBlockEntity blockEntity;

    public ProgressFluidTank(int capacity, SapCollectorBlockEntity blockEntity) {
        super(capacity);
        this.blockEntity = blockEntity;
    }

    @Override
    public @NotNull FluidStack drain(int maxDrain, IFluidHandler.FluidAction action) {
        FluidStack drained = super.drain(maxDrain, action);
        if (this.blockEntity.currentRecipe != null && !drained.isEmpty()) {
            int drainedProgress = Math.max(1, (int) (this.blockEntity.currentRecipe.value().processingTime / 1000f * drained.getAmount()));
            if (this.blockEntity.progress > drainedProgress) {
                if (action.equals(IFluidHandler.FluidAction.EXECUTE)) {
                    this.blockEntity.progress -= drainedProgress;
                    if (this.blockEntity.getLevel() != null) {
                        this.blockEntity.getLevel().sendBlockUpdated(this.blockEntity.getBlockPos(), this.blockEntity.getBlockState(), this.blockEntity.getBlockState(), Block.UPDATE_CLIENTS);
                    }
                }
            } else {
                drained.setAmount(0);
            }
        }
        return drained;
    }
}