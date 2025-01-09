package slimeknights.tmechworks.common.inventory.slots;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;

public class ValidatingSlot extends Slot {
    public ValidatingSlot(IInventory inventoryIn, int index, int xPosition, int yPosition) {
        super(inventoryIn, index, xPosition, yPosition);
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        if(container instanceof ISlotValidate)
            return ((ISlotValidate)container).isItemValidForValidatingSlot(getSlotIndex(), stack);
        else
            return container.canPlaceItem(getSlotIndex(), stack);
    }
}
