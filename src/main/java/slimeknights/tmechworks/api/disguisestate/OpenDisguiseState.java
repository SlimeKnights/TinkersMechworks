package slimeknights.tmechworks.api.disguisestate;

import net.minecraft.state.properties.BlockStateProperties;

public class OpenDisguiseState extends BasicDisguiseState<Boolean> {
    public OpenDisguiseState() {
        super(BlockStateProperties.OPEN, false);
    }

    @Override
    public int getIconFor(Boolean value) {
        return value ? 4 : 5;
    }
}
