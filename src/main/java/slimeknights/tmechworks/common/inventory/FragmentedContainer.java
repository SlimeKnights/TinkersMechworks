package slimeknights.tmechworks.common.inventory;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import slimeknights.mantle.block.entity.MantleBlockEntity;
import slimeknights.tmechworks.common.inventory.slots.ISlotValidate;

import java.util.function.Predicate;

public class FragmentedContainer implements Container, ISlotValidate {
    private final Container parent;
    private final int startSlot;
    private int size;

    private boolean overrideStackLimit;
    private int stackLimit = 64;
    private Predicate<ItemStack> validItems = stack -> true;

    public FragmentedContainer(Container parent, int startSlot, int size) {
        this.parent = parent;
        this.startSlot = startSlot;
        this.size = size;
    }

    @Override
    public int getContainerSize() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        for (int i = 0; i < getContainerSize(); i++) {
            if (!getItem(i).isEmpty()) {
                return false;
            }
        }

        return true;
    }

    @Override
    public ItemStack getItem(int slot) {
        if (!isSlotInInventory(slot))
            return ItemStack.EMPTY;

        return parent.getItem(getSlot(slot));
    }

    @Override
    public ItemStack removeItem(int slot, int count) {
        if (!isSlotInInventory(slot))
            return ItemStack.EMPTY;

        return parent.removeItem(getSlot(slot), count);
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        if (!isSlotInInventory(slot))
            return ItemStack.EMPTY;

        return parent.removeItemNoUpdate(getSlot(slot));
    }

    @Override
    public void setItem(int slot, ItemStack itemStack) {
        if (isSlotInInventory(slot))
            parent.setItem(getSlot(slot), itemStack);
    }

    public int getSlot(int slot){
        return slot + startSlot;
    }

    @Override
    public void setChanged() {
        parent.setChanged();
    }

    /**
     * Calls regular markDirty if not child of MantleTileEntity
     */
    public void markDirtyFast() {
        if (parent instanceof MantleBlockEntity)
            ((MantleBlockEntity) parent).setChangedFast();
        else
            setChanged();
    }

    @Override
    public boolean stillValid(Player playerEntity) {
        return parent.stillValid(playerEntity);
    }

    @Override
    public void clearContent() {
        for (int slot = 0; slot < getContainerSize(); slot++) {
            setItem(slot, ItemStack.EMPTY);
        }
    }

    @Override
    public int getMaxStackSize() {
        if(overrideStackLimit)
            return stackLimit;

        return parent.getMaxStackSize();
    }

    public FragmentedContainer overrideStackLimit(int stackLimit) {
        this.overrideStackLimit = true;
        this.stackLimit = stackLimit;

        return this;
    }

    public FragmentedContainer setValidItemsPredicate(Predicate<ItemStack> validItemsPredicate) {
        validItems = validItemsPredicate;

        return this;
    }

    @Override
    public void startOpen(Player player) {
        parent.startOpen(player);
    }

    @Override
    public void stopOpen(Player player) {
        parent.stopOpen(player);
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack itemStack) {
        return isItemValidForValidatingSlot(slot, itemStack);
    }

    public boolean isSlotInInventory(int i) {
        return i >= 0 && i < size && i + startSlot < parent.getContainerSize();
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
