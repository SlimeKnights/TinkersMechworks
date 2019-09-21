package slimeknights.tmechworks.common.inventory.slots;

import net.minecraft.item.ItemStack;

public interface ISlotValidate {
    boolean isItemValidForValidatingSlot(int slot, ItemStack item);
}
