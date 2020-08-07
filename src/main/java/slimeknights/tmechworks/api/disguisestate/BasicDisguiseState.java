package slimeknights.tmechworks.api.disguisestate;

import net.minecraft.block.BlockState;
import net.minecraft.state.Property;

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
        return state.with(property, getValueFrom(value));
    }

    @Override
    public Collection<T> getAllowedValues() {
        return property.getAllowedValues();
    }

    @Override
    public T getValueFrom(String value) {
        return property.parseValue(value).orElse(defaultValue);
    }
}
