package tmechworks.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import tconstruct.library.TConstructRegistry;
import tconstruct.library.blocks.IDrawbridgeLogicBase;

public class DrawbridgeSlot extends SlotBlocksOnly
{
    IDrawbridgeLogicBase logic;

    public DrawbridgeSlot(IInventory iinventory, int par2, int par3, int par4, IDrawbridgeLogicBase logic)
    {
        super(iinventory, par2, par3, par4);
        this.logic = logic;
    }

    @Override
    public boolean isItemValid (ItemStack par1ItemStack)
    {
        if (!super.isItemValid(par1ItemStack))
        {
            if (TConstructRegistry.blockToItemMapping[par1ItemStack.itemID] == 0)
            {
                return false;
            }
        }

        return !logic.hasExtended();
    }

    @Override
    public boolean canTakeStack (EntityPlayer par1EntityPlayer)
    {
        return !logic.hasExtended();
    }

    @Override
    public int getSlotStackLimit ()
    {
        return inventory.getInventoryStackLimit();
    }
}
