package tmechworks.inventory;

import mantle.blocks.BlockUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import tmechworks.lib.TMechworksRegistry;
import tmechworks.lib.blocks.IDrawbridgeLogicBase;

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
        if (par1ItemStack == null || !(par1ItemStack.getItem() instanceof ItemBlock))
        {
            return false;
        }

        if (TMechworksRegistry.isItemDBBlacklisted((ItemBlock) par1ItemStack.getItem()))
        {
            return false;
        }

        if (!super.isItemValid(par1ItemStack))
        {
            if (BlockUtils.getBlockFromItem((TMechworksRegistry.blockToItemMapping.get(BlockUtils.getBlockFromItem(par1ItemStack.getItem())))) == Blocks.air)
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
