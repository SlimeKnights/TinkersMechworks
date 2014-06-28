package tmechworks.blocks.logic;

import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;

//Filters out itemBlocks, equipment, and armor.
public class MeshFilter extends SlatFilter
{

    @Override
    public boolean canPass (ItemStack itemStack)
    {
        Item item = itemStack.getItem();
        if ((((item instanceof ItemTool) || (item instanceof ItemSword)) || (item instanceof ItemBow)) || (item instanceof ItemArmor))
        {
            return false;
        }
        return super.canPass(itemStack);
    }
}
