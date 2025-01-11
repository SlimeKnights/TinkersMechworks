package slimeknights.tmechworks.common.inventory;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.BlockPos;
import slimeknights.mantle.inventory.BaseContainerMenu;
import slimeknights.tmechworks.common.MechworksContent;
import slimeknights.tmechworks.common.blocks.entity.RedstoneMachineBlockEntity;
import slimeknights.tmechworks.common.inventory.slots.ValidatingSlot;

public class DisguiseContainerMenu extends BaseContainerMenu<RedstoneMachineBlockEntity> {
    public DisguiseContainerMenu(int windowId, Inventory playerInventory, RedstoneMachineBlockEntity tile) {
        super(MechworksContent.Containers.disguise.get(), windowId, playerInventory, tile);

        tile.startOpen(playerInventory.player);

        addSlot(new ValidatingSlot(tile.getDisguiseInventory(), 0, 80, 34));
        addInventorySlots();
    }

    public static DisguiseContainerMenu factory(int id, Inventory playerInventory, FriendlyByteBuf extraData) {
        BlockPos pos = extraData.readBlockPos();

        BlockEntity te = playerInventory.player.level.getBlockEntity(pos);
        RedstoneMachineBlockEntity machine = null;

        if(te instanceof RedstoneMachineBlockEntity)
            machine = (RedstoneMachineBlockEntity) te;

        return new DisguiseContainerMenu(id, playerInventory, machine);
    }
}
