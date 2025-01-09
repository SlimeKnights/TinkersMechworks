package slimeknights.tmechworks.api.disguisestate;

import net.minecraft.state.properties.BlockStateProperties;

public class PicklesDisguiseState extends BasicDisguiseState<Integer> {
    public PicklesDisguiseState() {
        super(BlockStateProperties.PICKLES, 1);
    }

    @Override
    public int getIconFor(Integer value) {
        return 7 + value;
    }
}
