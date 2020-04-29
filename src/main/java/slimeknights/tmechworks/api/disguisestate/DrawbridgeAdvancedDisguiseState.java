package slimeknights.tmechworks.api.disguisestate;

import slimeknights.tmechworks.common.blocks.DrawbridgeBlock;

public class DrawbridgeAdvancedDisguiseState extends BasicDisguiseState<Boolean> {
    public DrawbridgeAdvancedDisguiseState() {
        super(DrawbridgeBlock.ADVANCED, false);
    }

    @Override
    public int getIconFor(Boolean value) {
        return value ? 27 : 26;
    }
}
