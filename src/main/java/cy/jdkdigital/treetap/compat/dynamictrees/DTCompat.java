package cy.jdkdigital.treetap.compat.dynamictrees;

import com.ferreusveritas.dynamictrees.api.treedata.TreePart;
import com.ferreusveritas.dynamictrees.block.branch.BranchBlock;
import com.ferreusveritas.dynamictrees.worldgen.deserialisation.JsonMath;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.TFCBlockStateProperties;
import net.dries007.tfc.common.blocks.wood.BranchDirection;
import net.dries007.tfc.util.Helpers;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;

public class DTCompat {
    public static boolean isValidTree(LevelReader level, BlockPos pos) {
        var branchState = level.getBlockState(pos);
        if(branchState.getBlock() instanceof BranchBlock) {
            int radius = ((BranchBlock) branchState.getBlock()).getRadius(branchState);
            return radius == 8;
        }
        return false;
    }
}
