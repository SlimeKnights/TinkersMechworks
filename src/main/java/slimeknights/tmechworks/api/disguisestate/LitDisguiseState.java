package slimeknights.tmechworks.api.disguisestate;

import net.minecraft.state.properties.BlockStateProperties;

public class LitDisguiseState extends BasicDisguiseState<Boolean> {
    public LitDisguiseState() {
        super(BlockStateProperties.LIT, false);
    }

    @Override
    public int getIconFor(Boolean value) {
        return value ? 6 : 7;
    }
}
