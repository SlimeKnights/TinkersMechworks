package slimeknights.tmechworks.api.disguisestate;

import net.minecraft.state.properties.BlockStateProperties;

public class HasBookDisguiseState extends BasicDisguiseState<Boolean> {
    public HasBookDisguiseState() {
        super(BlockStateProperties.HAS_BOOK, false);
    }

    @Override
    public int getIconFor(Boolean value) {
        return value ? 27 : 26;
    }
}
