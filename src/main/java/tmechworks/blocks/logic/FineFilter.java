package tmechworks.blocks.logic;

import java.util.ArrayList;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class FineFilter extends MeshFilter
{

    public ArrayList<Item> blacklist = new ArrayList<Item>();
    public ArrayList<Item> whitelist = new ArrayList<Item>();

    public ArrayList<String> searchTerms = new ArrayList<String>(8);

    @Override
    public boolean canPass (ItemStack itemStack)
    {
        Item item = itemStack.getItem();
        if (blacklist.contains(item))
        {
            return false;
        }
        if (whitelist.contains(item))
        {
            return true;
        }
        for (int i = 0; i < searchTerms.size(); ++i)
        {
            if (itemStack.getUnlocalizedName().toLowerCase().contains(searchTerms.get(i).toLowerCase()))
            {
                return true;
            }
        }
        return false;
    }

    public FineFilter()
    {
        super();
        searchTerms.add("dust");
        searchTerms.add("powder");
        whitelist.add(Items.glowstone_dust);
        whitelist.add(Items.redstone);
        whitelist.add(Items.gunpowder);
        //whitelist.add(Items.sand);
    }
}
