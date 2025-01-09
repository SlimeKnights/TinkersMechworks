package slimeknights.tmechworks.api.disguisestate;

import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class OpenDisguiseState extends BasicDisguiseState<Boolean> {
    public OpenDisguiseState() {
        super(BlockStateProperties.OPEN, false);
    }

    @Override
    public int getIconFor(Boolean value) {
        return value ? 4 : 5;
    }
}
