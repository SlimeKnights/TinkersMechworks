package slimeknights.tmechworks.api.disguisestate;

import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class LayersDisguiseState extends BasicDisguiseState<Integer> {
    public LayersDisguiseState() {
        super(BlockStateProperties.LAYERS, 1);
    }

    @Override
    public int getIconFor(Integer value) {
        return 13 + value;
    }
}
