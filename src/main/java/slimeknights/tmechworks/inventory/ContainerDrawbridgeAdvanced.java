package slimeknights.tmechworks.inventory;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import slimeknights.mantle.inventory.BaseContainer;
import slimeknights.tmechworks.blocks.logic.drawbridge.DrawbridgeLogicBase;

public class ContainerDrawbridgeAdvanced extends BaseContainer<DrawbridgeLogicBase>
{
    public InventoryPlayer inventoryPlayer;

    public ContainerDrawbridgeAdvanced (DrawbridgeLogicBase tile, InventoryPlayer inventoryPlayer)
    {
        super(tile);

        this.inventoryPlayer = inventoryPlayer;

        //TODO: Uncomment when disguise done
        //addSlotToContainer(new SlotDisguise(tile, 35, 36));
        for(int i = 0; i < tile.getSizeInventory(); i++)
            addSlotToContainer(new Slot(tile, i, 80 + i * 16, 36));

        addPlayerInventory(inventoryPlayer, 8, 84);
    }
}
