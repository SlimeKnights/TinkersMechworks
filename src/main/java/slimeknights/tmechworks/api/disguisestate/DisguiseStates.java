package slimeknights.tmechworks.api.disguisestate;

import net.minecraft.block.BlockState;
import net.minecraft.state.properties.BlockStateProperties;

import java.util.ArrayList;

public final class DisguiseStates {
    public static ArrayList<DisguiseState<?>> disguiseStates = new ArrayList<>();

    public static final SlabTypeDisguiseState SLAB_TYPE = new SlabTypeDisguiseState();

    public static DisguiseState<?> getForState(BlockState state) {
        for(DisguiseState<?> ds : DisguiseStates.disguiseStates) {
            if(ds.canApplyTo(state))
                return ds;
        }

        return null;
    }

    public static BlockState processDisguiseStates(BlockState state, String disguiseState) {
        if (state.has(BlockStateProperties.FACING))
            state = state.with(BlockStateProperties.FACING, state.get(BlockStateProperties.FACING));

        DisguiseState<?> ds = getForState(state);
        if(ds != null) {
            return ds.apply(state, disguiseState);
        }

        return state;
    }

    static {
        disguiseStates.add(SLAB_TYPE);
    }
}
