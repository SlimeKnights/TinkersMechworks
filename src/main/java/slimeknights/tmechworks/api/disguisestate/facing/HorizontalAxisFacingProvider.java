package slimeknights.tmechworks.api.disguisestate.facing;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.core.Direction;

public class HorizontalAxisFacingProvider extends FacingProvider {
    @Override
    public boolean canApplyTo(BlockState state, Direction facing) {
        return state.hasProperty(BlockStateProperties.HORIZONTAL_AXIS) && facing != Direction.UP && facing != Direction.DOWN;
    }

    @Override
    public BlockState applyTo(BlockState state, Direction facing) {
        return state.setValue(BlockStateProperties.HORIZONTAL_AXIS, facing.getAxis());
    }
}
