package slimeknights.tmechworks.common.inventory;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import slimeknights.mantle.tileentity.MantleTileEntity;
import slimeknights.tmechworks.common.inventory.slots.ISlotValidate;

import java.util.function.Predicate;

public class FragmentedInventory implements IInventory, ISlotValidate {
    private final IInventory parent;
    private final int startSlot;
    private int size;

    private boolean overrideStackLimit;
    private int stackLimit = 64;
    private Predicate<ItemStack> validItems = stack -> true;

    public FragmentedInventory(IInventory parent, int startSlot, int size) {
        this.parent = parent;
        this.startSlot = startSlot;
        this.size = size;
    }

    @Override
    public int getSizeInventory() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        for (int i = 0; i < getSizeInventory(); i++) {
            if (!getStackInSlot(i).isEmpty()) {
                return false;
            }
        }

        return true;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        if (!isSlotInInventory(slot))
            return ItemStack.EMPTY;

        return parent.getStackInSlot(getSlot(slot));
    }

    @Override
    public ItemStack decrStackSize(int slot, int count) {
        if (!isSlotInInventory(slot))
            return ItemStack.EMPTY;

        return parent.decrStackSize(getSlot(slot), count);
    }

    @Override
    public ItemStack removeStackFromSlot(int slot) {
        if (!isSlotInInventory(slot))
            return ItemStack.EMPTY;

        return parent.removeStackFromSlot(getSlot(slot));
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack itemStack) {
        if (isSlotInInventory(slot))
            parent.setInventorySlotContents(getSlot(slot), itemStack);
    }

    public int getSlot(int slot){
        return slot + startSlot;
    }

    @Override
    public void markDirty() {
        parent.markDirty();
    }

    /**
     * Calls regular markDirty if not child of MantleTileEntity
     */
    public void markDirtyFast() {
        if (parent instanceof MantleTileEntity)
            ((MantleTileEntity) parent).markDirtyFast();
        else
            markDirty();
    }

    @Override
    public boolean isUsableByPlayer(PlayerEntity playerEntity) {
        return parent.isUsableByPlayer(playerEntity);
    }

    @Override
    public void clear() {
        for (int slot = 0; slot < getSizeInventory(); slot++) {
            setInventorySlotContents(slot, ItemStack.EMPTY);
        }
    }

    @Override
    public int getInventoryStackLimit() {
        if(overrideStackLimit)
            return stackLimit;

        return parent.getInventoryStackLimit();
    }

    public FragmentedInventory overrideStackLimit(int stackLimit) {
        this.overrideStackLimit = true;
        this.stackLimit = stackLimit;

        return this;
    }

    public FragmentedInventory setValidItemsPredicate(Predicate<ItemStack> validItemsPredicate) {
        validItems = validItemsPredicate;

        return this;
    }

    @Override
    public void openInventory(PlayerEntity player) {
        parent.openInventory(player);
    }

    @Override
    public void closeInventory(PlayerEntity player) {
        parent.closeInventory(player);
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack itemStack) {
        return isItemValidForValidatingSlot(slot, itemStack);
    }

    public boolean isSlotInInventory(int i) {
        return i >= 0 && i < size && i + startSlot < parent.getSizeInventory();
    }

    public void resize(int newSize){
        size = newSize;
    }

    public int getStartSlot() {
        return startSlot;
    }

    @Override
    public boolean isItemValidForValidatingSlot(int slot, ItemStack item) {
        return validItems.test(item);
    }
}
