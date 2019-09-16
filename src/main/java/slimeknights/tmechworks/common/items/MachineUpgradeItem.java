package slimeknights.tmechworks.common.items;

import net.minecraft.item.Item;
import slimeknights.tmechworks.common.MechworksContent;
import slimeknights.tmechworks.common.blocks.tileentity.DrawbridgeTileEntity;

import java.util.function.Consumer;

public class MachineUpgradeItem extends MechworksItem {
    public final Consumer<DrawbridgeTileEntity.DrawbridgeStats> effect;

    public MachineUpgradeItem(Consumer<DrawbridgeTileEntity.DrawbridgeStats> effect) {
        super(new Item.Properties().group(MechworksContent.tabMechworks));

        this.effect = effect;
    }
}
