package slimeknights.tmechworks.common.inventory.slots;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class ValidatingSlot extends Slot {
    public ValidatingSlot(Container inventoryIn, int index, int xPosition, int yPosition) {
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
