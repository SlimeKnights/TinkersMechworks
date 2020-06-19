package slimeknights.tmechworks.api.disguisestate.facing;

import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;

import java.util.ArrayList;

public class FacingProviders {
    public static ArrayList<FacingProvider> facingProviders = new ArrayList<>();

    public static final FacingProvider FACING = new FacingProvider();
    public static final FacingProvider FACING_EXCEPT_UP = new FacingExceptUpFacingProvider();
    public static final FacingProvider HORIZONTAL_FACING = new HorizontalFacingProvider();

    public static final FacingProvider AXIS = new AxisFacingProvider();
    public static final FacingProvider HORIZONTAL_AXIS = new HorizontalAxisFacingProvider();

    public static BlockState processFacingFor(BlockState state, Direction facing) {
        for(FacingProvider provider : facingProviders) {
            if(provider.canApplyTo(state, facing)) {
                return provider.applyTo(state, facing);
            }
        }

        return state;
    }

    static {
        facingProviders.add(FACING);
        facingProviders.add(FACING_EXCEPT_UP);
        facingProviders.add(HORIZONTAL_FACING);

        facingProviders.add(AXIS);
        facingProviders.add(HORIZONTAL_AXIS);
    }
}
