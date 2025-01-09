package slimeknights.tmechworks.api.disguisestate;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import slimeknights.tmechworks.TMechworks;

import java.util.Collection;

public abstract class DisguiseState<T extends Comparable<T>> {
    public abstract boolean canApplyTo(BlockState state);

    public abstract BlockState apply(BlockState state, String value);

    public abstract Collection<T> getAllowedValues();
    public abstract T getValueFrom(String state);

    @OnlyIn(Dist.CLIENT)
    public ResourceLocation getIconSheet() {
        return new ResourceLocation(TMechworks.modId, "textures/gui/disguise_states.png");
    }

    @OnlyIn(Dist.CLIENT)
    @SuppressWarnings("unchecked")
    public final int unsafeGetIconFor(Object value) {
        return getIconFor((T) value);
    }

    @OnlyIn(Dist.CLIENT)
    public abstract int getIconFor(T value);
}
