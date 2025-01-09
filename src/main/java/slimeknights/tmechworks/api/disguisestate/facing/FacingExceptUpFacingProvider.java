package slimeknights.tmechworks.api.disguisestate.facing;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.core.Direction;

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
