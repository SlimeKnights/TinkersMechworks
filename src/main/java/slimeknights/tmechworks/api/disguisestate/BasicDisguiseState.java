package slimeknights.tmechworks.api.disguisestate;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

import java.util.Collection;

public abstract class BasicDisguiseState<T extends Comparable<T>> extends DisguiseState<T> {
    private final Property<T> property;
    private final T defaultValue;

    public BasicDisguiseState(Property<T> property, T defaultValue) {
        this.property = property;
        this.defaultValue = defaultValue;
    }

    @Override
    public boolean canApplyTo(BlockState state) {
        return state.hasProperty(property);
    }

    @Override
    public BlockState apply(BlockState state, String value) {
        return state.setValue(property, getValueFrom(value));
    }

    @Override
    public Collection<T> getAllowedValues() {
        return property.getPossibleValues();
    }

    @Override
    public T getValueFrom(String value) {
        return property.getValue(value).orElse(defaultValue);
    }
}
