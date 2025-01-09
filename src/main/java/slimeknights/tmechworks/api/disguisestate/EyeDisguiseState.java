package slimeknights.tmechworks.api.disguisestate;

import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class EyeDisguiseState extends BasicDisguiseState<Boolean> {
    public EyeDisguiseState() {
        super(BlockStateProperties.EYE, false);
    }

    @Override
    public int getIconFor(Boolean value) {
        return value ? 23 : 22;
    }
}
