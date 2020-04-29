package slimeknights.tmechworks.api.disguisestate;

import net.minecraft.state.properties.BlockStateProperties;

public class LayersDisguiseState extends BasicDisguiseState<Integer> {
    public LayersDisguiseState() {
        super(BlockStateProperties.LAYERS_1_8, 1);
    }

    @Override
    public int getIconFor(Integer value) {
        return 13 + value;
    }
}
