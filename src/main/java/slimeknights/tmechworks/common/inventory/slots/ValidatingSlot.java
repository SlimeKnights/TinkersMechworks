package slimeknights.tmechworks.common.inventory.slots;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;

public class ValidatingSlot extends Slot {
    public ValidatingSlot(IInventory inventoryIn, int index, int xPosition, int yPosition) {
        super(inventoryIn, index, xPosition, yPosition);
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
        if(inventory instanceof ISlotValidate)
            return ((ISlotValidate)inventory).isItemValidForValidatingSlot(getSlotIndex(), stack);
        else
            return inventory.isItemValidForSlot(getSlotIndex(), stack);
    }
}
