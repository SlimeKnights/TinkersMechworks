package slimeknights.tmechworks.common.inventory;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.BlockPos;
import slimeknights.mantle.inventory.BaseContainerMenu;
import slimeknights.tmechworks.common.MechworksContent;
import slimeknights.tmechworks.common.blocks.tileentity.RedstoneMachineTileEntity;
import slimeknights.tmechworks.common.inventory.slots.ValidatingSlot;

public class DisguiseContainer extends BaseContainerMenu<RedstoneMachineTileEntity> {
    public DisguiseContainer(int windowId, Inventory playerInventory, RedstoneMachineTileEntity tile) {
        super(MechworksContent.Containers.disguise.get(), windowId, playerInventory, tile);

        tile.startOpen(playerInventory.player);

        addSlot(new ValidatingSlot(tile.getDisguiseInventory(), 0, 80, 34));
        addInventorySlots();
    }

    public static DisguiseContainer factory(int id, Inventory playerInventory, FriendlyByteBuf extraData) {
        BlockPos pos = extraData.readBlockPos();

        BlockEntity te = playerInventory.player.level.getBlockEntity(pos);
        RedstoneMachineTileEntity machine = null;

        if(te instanceof RedstoneMachineTileEntity)
            machine = (RedstoneMachineTileEntity) te;

        return new DisguiseContainer(id, playerInventory, machine);
    }
}
