package slimeknights.tmechworks.common.items;

import slimeknights.tmechworks.common.blocks.tileentity.DrawbridgeTileEntity;

import java.util.function.Consumer;

public class MachineUpgradeItem extends MechworksItem {
    public final Consumer<DrawbridgeTileEntity.DrawbridgeStats> effect;

    public MachineUpgradeItem(Consumer<DrawbridgeTileEntity.DrawbridgeStats> effect) {
        this.effect = effect;
    }
}
