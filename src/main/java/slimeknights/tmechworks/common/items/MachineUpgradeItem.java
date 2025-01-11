package slimeknights.tmechworks.common.items;

import slimeknights.tmechworks.common.blocks.entity.DrawbridgeBlockEntity;

import java.util.function.Consumer;

public class MachineUpgradeItem extends MechworksItem {
    public final Consumer<DrawbridgeBlockEntity.DrawbridgeStats> effect;

    public MachineUpgradeItem(Consumer<DrawbridgeBlockEntity.DrawbridgeStats> effect) {
        this.effect = effect;
    }
}
