package slimeknights.tmechworks.api.disguisestate.facing;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.core.Direction;

public class FacingProvider {
    public boolean canApplyTo(BlockState state, Direction facing) {
        return state.hasProperty(BlockStateProperties.FACING);
    }

    public BlockState applyTo(BlockState state, Direction facing) {
        return state.setValue(BlockStateProperties.FACING, facing);
    }
}
