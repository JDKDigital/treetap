package cy.jdkdigital.treetap.common.block.entity;

import cy.jdkdigital.treetap.TreeTap;
import cy.jdkdigital.treetap.common.recipe.TapExtractRecipe;
import cy.jdkdigital.treetap.util.ProgressFluidTank;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

public class SapCollectorBlockEntity extends BlockEntity
{
    public int progress = 0;
    public RecipeHolder<TapExtractRecipe> currentRecipe;

    private IItemHandlerModifiable inventoryHandler = new ItemStackHandler(1)
    {
        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            // No inserting
            return false;
        }

        @Override
        public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
            ItemStack out = super.extractItem(slot, amount, simulate);
            if (!simulate && getStackInSlot(slot).isEmpty()) {
                progress = 0;
            }
            return out;
        }
    };

    public IFluidHandler fluidHandler = new ProgressFluidTank(1000, this);

    public SapCollectorBlockEntity(BlockPos pos, BlockState state) {
        super(TreeTap.SAP_COLLECTOR_BLOCK_ENTITY.get(), pos, state);
    }

    public void setCurrentRecipe(RecipeHolder<TapExtractRecipe> recipe) {
        this.currentRecipe = recipe;
        // Reset fluid handler
        if (currentRecipe != null) {
            var itemCap = currentRecipe.value().getResultItem(getBlockState()).getCapability(Capabilities.FluidHandler.ITEM);
            if (itemCap != null) {
                fluidHandler = new ProgressFluidTank(1000, this);
                if (progress > 0) {
                    itemCap.fill(new FluidStack(fluidHandler.getFluidInTank(0).getFluid(), (int) (fluidHandler.getTankCapacity(0) * ((float) progress / (float) currentRecipe.value().processingTime))), IFluidHandler.FluidAction.EXECUTE);
                }
            };
        }
    }

    public void addProgress(int progress) {
        if (currentRecipe != null) {
            this.progress += progress;

            var fluidCap = currentRecipe.value().getResultItem(getBlockState()).getCapability(Capabilities.FluidHandler.ITEM);
            if (fluidCap != null) {
                if (fluidHandler.getFluidInTank(0).isEmpty()) {
                    fluidHandler.fill(new FluidStack(fluidCap.getFluidInTank(0).getFluid(), (int)(1000f * ((float)this.progress / (float)currentRecipe.value().processingTime))), IFluidHandler.FluidAction.EXECUTE);
                } else {
                    fluidHandler.getFluidInTank(0).setAmount((int)(1000f * ((float)this.progress / (float)currentRecipe.value().processingTime)));
                }
            } else if (this.progress >= currentRecipe.value().processingTime) {
                inventoryHandler.setStackInSlot(0, currentRecipe.value().getResultItem(getBlockState()));
            }
        }
    }

    @Override
    protected void loadAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        this.loadPacketNBT(pTag, pRegistries);
    }

    @Override
    protected void saveAdditional(CompoundTag pTag, HolderLookup.Provider pRegistries) {
        super.saveAdditional(pTag, pRegistries);
        this.savePacketNBT(pTag, pRegistries);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.Provider pRegistries) {
        return saveWithId(pRegistries);
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt, HolderLookup.Provider lookupProvider) {
        super.onDataPacket(net, pkt, lookupProvider);
        this.loadPacketNBT(pkt.getTag(), lookupProvider);
        if (level instanceof ClientLevel) {
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 0);
        }
    }

    public void loadPacketNBT(CompoundTag tag, HolderLookup.Provider lookupProvider) {
        this.progress = tag.getInt("progress");
        if (tag.contains("recipe") && level != null) {
            var recipe = level.getRecipeManager().byKey(ResourceLocation.parse(tag.getString("recipe")));
            recipe.ifPresent(value -> this.setCurrentRecipe((RecipeHolder<TapExtractRecipe>) value));
        }
    }

    public void savePacketNBT(CompoundTag tag, HolderLookup.Provider lookupProvider) {
        tag.putInt("progress", progress);
        if (this.currentRecipe != null) {
            tag.putString("recipe", this.currentRecipe.id().toString());
        }
    }
}
