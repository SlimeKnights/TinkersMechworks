package slimeknights.tmechworks.api.disguisestate.facing;

import net.minecraft.block.BlockState;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;

public class FacingExceptUpFacingProvider extends FacingProvider {
    @Override
    public boolean canApplyTo(BlockState state, Direction facing) {
        return state.has(BlockStateProperties.FACING_EXCEPT_UP) && facing != Direction.UP;
    }

    @Override
    public BlockState applyTo(BlockState state, Direction facing) {
        return state.with(BlockStateProperties.FACING_EXCEPT_UP, facing);
    }
}
