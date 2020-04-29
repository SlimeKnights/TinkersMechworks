package slimeknights.tmechworks.api.disguisestate;

import slimeknights.tmechworks.common.blocks.FirestarterBlock;

public class FirestarterExtinguishDisguiseState extends BasicDisguiseState<Boolean> {
    public FirestarterExtinguishDisguiseState() {
        super(FirestarterBlock.EXTINGUISH, false);
    }

    @Override
    public int getIconFor(Boolean value) {
        return value ? 7 : 6;
    }
}
