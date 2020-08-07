package slimeknights.tmechworks.api.disguisestate.facing;

import net.minecraft.block.BlockState;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;

public class AxisFacingProvider extends FacingProvider {
    @Override
    public boolean canApplyTo(BlockState state, Direction facing) {
        return state.hasProperty(BlockStateProperties.AXIS);
    }

    @Override
    public BlockState applyTo(BlockState state, Direction facing) {
        return state.with(BlockStateProperties.AXIS, facing.getAxis());
    }
}
