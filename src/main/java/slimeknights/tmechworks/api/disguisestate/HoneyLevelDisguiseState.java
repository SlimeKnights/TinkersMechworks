package slimeknights.tmechworks.api.disguisestate;

import com.google.common.collect.ImmutableSet;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import java.util.Collection;

public class HoneyLevelDisguiseState extends BasicDisguiseState<Integer> {
    public HoneyLevelDisguiseState() {
        super(BlockStateProperties.LEVEL_HONEY, 1);
    }

    @Override
    public Collection<Integer> getAllowedValues() {
        return ImmutableSet.of(0, 5);
    }

    @Override
    public int getIconFor(Integer value) {
        return value == 0 ? 24 : 25;
    }
}
