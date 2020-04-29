package slimeknights.tmechworks.api.disguisestate;

import com.google.common.collect.ImmutableSet;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.SlabType;

import java.util.Collection;

public class SlabTypeDisguiseState extends BasicDisguiseState<SlabType> {
    public SlabTypeDisguiseState() {
        super(BlockStateProperties.SLAB_TYPE, SlabType.BOTTOM);
    }

    @Override
    public int getIconFor(SlabType value) {
        switch(value) {
            case BOTTOM:
                return 1;
            case TOP:
                return 2;
            case DOUBLE:
                return 3;
        }

        return 0;
    }

    @Override
    public Collection<SlabType> getAllowedValues() {
        // Overridden for specific order
        return ImmutableSet.of(SlabType.TOP, SlabType.DOUBLE, SlabType.BOTTOM);
    }
}
