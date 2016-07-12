package slimeknights.tmechworks.inventory;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import slimeknights.tmechworks.blocks.logic.IDisguisable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SlotDisguise extends Slot
{
    private IDisguisable disguise;

    public SlotDisguise (@Nonnull IDisguisable disguisable, int xPosition, int yPosition)
    {
        super(null, 0, xPosition, yPosition);
        this.disguise = disguisable;
    }

    /**
     * Check if the stack is a valid item for this slot. Always true beside for the armor slots.
     */
    public boolean isItemValid (@Nullable ItemStack stack)
    {
        if (!disguise.canEditDisguise() || stack == null)
        {
            return false;
        }

        Block block = Block.getBlockFromItem(stack.getItem());

        return block != null && block.isNormalCube(block.getDefaultState(), null, null);
    }

    /**
     * Helper fnct to get the stack in the slot.
     */
    @Nullable public ItemStack getStack ()
    {
        return disguise.getDisguiseBlock();
    }

    /**
     * Helper method to put a stack in the slot.
     */
    public void putStack (@Nullable ItemStack stack)
    {
        disguise.setDisguiseBlock(stack);
        this.onSlotChanged();
    }

    /**
     * Called when the stack in a Slot changes
     */
    public void onSlotChanged ()
    {
        disguise.markDirty();
    }

    /**
     * Returns the maximum stack size for a given slot (usually the same as getInventoryStackLimit(), but 1 in the case
     * of armor slots)
     */
    public int getSlotStackLimit ()
    {
        return 1;
    }

    /**
     * Decrease the size of the stack in slot (first int arg) by the amount of the second int arg. Returns the new
     * stack.
     */
    public ItemStack decrStackSize (int amount)
    {
        if (amount == 0)
        {
            return disguise.getDisguiseBlock();
        }
        disguise.setDisguiseBlock(null);
        return null;
    }

    /**
     * returns true if the slot exists in the given inventory and location
     */
    public boolean isHere (IInventory inv, int slotIn)
    {
        return false;
    }

    /**
     * Return whether this slot's stack can be taken from this slot.
     */
    public boolean canTakeStack (EntityPlayer playerIn)
    {
        return disguise.canEditDisguise();
    }

    public int getSlotIndex ()
    {
        return 0;
    }

    public boolean isSameInventory (Slot other)
    {
        return other instanceof SlotDisguise && ((SlotDisguise) other).disguise == disguise;
    }
}