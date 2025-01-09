package slimeknights.tmechworks.common.inventory.slots;

import net.minecraft.world.item.ItemStack;

public interface ISlotValidate {
    boolean isItemValidForValidatingSlot(int slot, ItemStack item);
}
