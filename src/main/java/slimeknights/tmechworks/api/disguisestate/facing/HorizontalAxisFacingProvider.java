package slimeknights.tmechworks.api.disguisestate.facing;

import net.minecraft.block.BlockState;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;

public class HorizontalAxisFacingProvider extends FacingProvider {
    @Override
    public boolean canApplyTo(BlockState state, Direction facing) {
        return state.hasProperty(BlockStateProperties.HORIZONTAL_AXIS) && facing != Direction.UP && facing != Direction.DOWN;
    }

    @Override
    public BlockState applyTo(BlockState state, Direction facing) {
        return state.with(BlockStateProperties.HORIZONTAL_AXIS, facing.getAxis());
    }
}
