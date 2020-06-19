package slimeknights.tmechworks.api.disguisestate.facing;

import net.minecraft.block.BlockState;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;

public class FacingProvider {
    public boolean canApplyTo(BlockState state, Direction facing) {
        return state.has(BlockStateProperties.FACING);
    }

    public BlockState applyTo(BlockState state, Direction facing) {
        return state.with(BlockStateProperties.FACING, facing);
    }
}
