//package cy.jdkdigital.treetap.compat.dynamictrees;
//
//import com.ferreusveritas.dynamictrees.block.branch.BranchBlock;
//import net.minecraft.core.BlockPos;
//import net.minecraft.world.level.LevelReader;
//
//public class DTCompat {
//    public static boolean isValidTree(LevelReader level, BlockPos pos) {
//        var branchState = level.getBlockState(pos);
//        if(branchState.getBlock() instanceof BranchBlock branchBlock) {
//            return branchBlock.getRadius(branchState) == 8;
//        }
//        return false;
//    }
//
//    public static boolean isCompatTree(LevelReader level, BlockPos pos) {
//        return level.getBlockState(pos).getBlock() instanceof BranchBlock;
//    }
//}
