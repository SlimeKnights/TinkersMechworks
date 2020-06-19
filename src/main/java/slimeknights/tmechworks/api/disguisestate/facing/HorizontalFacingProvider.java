package slimeknights.tmechworks.api.disguisestate.facing;

import net.minecraft.block.BlockState;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;

public class HorizontalFacingProvider extends FacingProvider {
    @Override
    public boolean canApplyTo(BlockState state, Direction facing) {
        return state.has(BlockStateProperties.HORIZONTAL_FACING) && facing != Direction.UP && facing != Direction.DOWN;
    }

    @Override
    public BlockState applyTo(BlockState state, Direction facing) {
        return state.with(BlockStateProperties.HORIZONTAL_FACING, facing);
    }
}
