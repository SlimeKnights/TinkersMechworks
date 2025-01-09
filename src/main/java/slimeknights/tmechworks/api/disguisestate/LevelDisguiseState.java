package slimeknights.tmechworks.api.disguisestate;

import net.minecraft.state.properties.BlockStateProperties;

public class LevelDisguiseState extends BasicDisguiseState<Integer> {
    public LevelDisguiseState() {
        super(BlockStateProperties.LEVEL_COMPOSTER, 1);
    }

    @Override
    public int getIconFor(Integer value) {
        if(value == 0)
            return 26;
        return 13 + value;
    }
}
