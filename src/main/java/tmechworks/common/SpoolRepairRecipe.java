package tmechworks.common;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

public class SpoolRepairRecipe implements IRecipe
{
    ItemStack spool;
    ItemStack wire;
    
    public SpoolRepairRecipe(ItemStack spool, ItemStack wire)
    {
        this.spool = spool.copy();
        this.spool.setItemDamage(256);
        this.wire = wire.copy();
    }
    @Override
    public boolean matches (InventoryCrafting inventorycrafting, World world)
    {
        int invLength = inventorycrafting.getSizeInventory();
        boolean foundSpool = false;
        int countWire = 0;
        ItemStack tmpStack;
        for (int i = 0; i < invLength; ++i)
        {
            tmpStack = inventorycrafting.getStackInSlot(i);
            if (tmpStack instanceof ItemStack)
            {
                if (tmpStack == spool)
                {
                    if (foundSpool)
                    {
                        return false;
                    }
                    foundSpool = true;
                } 
                else if (tmpStack == wire && tmpStack.getItemDamage() == wire.getItemDamage())
                {
                    ++countWire;
                }
                else
                {
                    return false;
                }
            }
            if (countWire > 0 && foundSpool)
            {
                return true;
            }
        }
        
        return false;
    }

    @Override
    public ItemStack getCraftingResult (InventoryCrafting inventorycrafting)
    {
        int invLength = inventorycrafting.getSizeInventory();
        ItemStack newSpool = null;
        int countWire = 0;
        ItemStack tmpStack;
        for (int i = 0; i < invLength; ++i)
        {
            tmpStack = inventorycrafting.getStackInSlot(i);
            if (tmpStack instanceof ItemStack)
            {
                if (tmpStack == spool)
                {
                    newSpool = tmpStack.copy();
                } 
                else if (tmpStack == wire && tmpStack.getItemDamage() == wire.getItemDamage())
                {
                    ++countWire;
                }
            }
        }
        if (countWire > 0 && newSpool != null)
        {
            newSpool.setItemDamage(newSpool.getItemDamage() - countWire);
        }
        
        return newSpool;
    }

    @Override
    public int getRecipeSize ()
    {
        // TODO Auto-generated method stub
        return 9;
    }

    @Override
    public ItemStack getRecipeOutput ()
    {
        return spool.copy();
    }

}
