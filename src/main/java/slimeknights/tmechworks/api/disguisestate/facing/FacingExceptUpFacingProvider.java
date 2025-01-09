package slimeknights.tmechworks.api.disguisestate.facing;

import net.minecraft.block.BlockState;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;

public class FacingExceptUpFacingProvider extends FacingProvider {
    @Override
    public boolean canApplyTo(BlockState state, Direction facing) {
        return state.hasProperty(BlockStateProperties.FACING_HOPPER) && facing != Direction.UP;
    }

    @Override
    public BlockState applyTo(BlockState state, Direction facing) {
        return state.setValue(BlockStateProperties.FACING_HOPPER, facing);
    }
}
