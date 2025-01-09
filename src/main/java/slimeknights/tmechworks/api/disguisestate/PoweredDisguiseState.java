package slimeknights.tmechworks.api.disguisestate;

import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class PoweredDisguiseState extends BasicDisguiseState<Boolean> {
    public PoweredDisguiseState() {
        super(BlockStateProperties.POWERED, false);
    }

    @Override
    public int getIconFor(Boolean value) {
        return value ? 29 : 28;
    }
}
