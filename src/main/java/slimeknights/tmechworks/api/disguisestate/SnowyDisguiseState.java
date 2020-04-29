package slimeknights.tmechworks.api.disguisestate;

import net.minecraft.state.properties.BlockStateProperties;

public class SnowyDisguiseState extends BasicDisguiseState<Boolean> {
    public SnowyDisguiseState() {
        super(BlockStateProperties.SNOWY, false);
    }

    @Override
    public int getIconFor(Boolean value) {
        return value ? 6 : 7;
    }
}
