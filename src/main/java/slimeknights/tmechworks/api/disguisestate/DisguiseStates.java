package slimeknights.tmechworks.api.disguisestate;

import net.minecraft.block.BlockState;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;

import java.util.ArrayList;

public final class DisguiseStates {
    public static ArrayList<DisguiseState<?>> disguiseStates = new ArrayList<>();

    public static final SlabTypeDisguiseState SLAB_TYPE = new SlabTypeDisguiseState();
    public static final OpenDisguiseState OPEN = new OpenDisguiseState();
    public static final LitDisguiseState LIT = new LitDisguiseState();
    public static final PicklesDisguiseState PICKLES = new PicklesDisguiseState();
    public static final LayersDisguiseState LAYERS = new LayersDisguiseState();
    public static final LevelDisguiseState LEVEL = new LevelDisguiseState();
    public static final EyeDisguiseState EYE = new EyeDisguiseState();
    public static final SnowyDisguiseState SNOWY = new SnowyDisguiseState();
    public static final HoneyLevelDisguiseState HONEY_LEVEL = new HoneyLevelDisguiseState();
    public static final HasBookDisguiseState HAS_BOOK = new HasBookDisguiseState();
    public static final PoweredDisguiseState POWERED = new PoweredDisguiseState();
    public static final FirestarterExtinguishDisguiseState FIRESTARTER_EXTINGUISH = new FirestarterExtinguishDisguiseState();
    public static final DrawbridgeAdvancedDisguiseState DRAWBRIDGE_ADVANCED = new DrawbridgeAdvancedDisguiseState();

    public static DisguiseState<?> getForState(BlockState state) {
        for(DisguiseState<?> ds : DisguiseStates.disguiseStates) {
            if(ds.canApplyTo(state))
                return ds;
        }

        return null;
    }

    public static BlockState processDisguiseStates(BlockState state, String disguiseState, Direction facing) {
        if (state.has(BlockStateProperties.FACING))
            state = state.with(BlockStateProperties.FACING, facing);

        DisguiseState<?> ds = getForState(state);
        if(ds != null) {
            return ds.apply(state, disguiseState);
        }

        return state;
    }

    static {
        disguiseStates.add(SLAB_TYPE);
        disguiseStates.add(OPEN);
        disguiseStates.add(LIT);
        disguiseStates.add(PICKLES);
        disguiseStates.add(LAYERS);
        disguiseStates.add(LEVEL);
        disguiseStates.add(EYE);
        disguiseStates.add(SNOWY);
        disguiseStates.add(HONEY_LEVEL);
//        disguiseStates.add(HAS_BOOK); // Not rendering
        disguiseStates.add(POWERED);

        disguiseStates.add(FIRESTARTER_EXTINGUISH);
        disguiseStates.add(DRAWBRIDGE_ADVANCED);
    }
}
