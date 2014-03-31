package tmechworks.items.blocks;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import tmechworks.lib.blocks.IBlockWithMetadata;

public class ItemBlockWithMetadata extends ItemBlock
{
    protected Block ourBlock;
    protected IBlockWithMetadata ourBlockMeta;

    public ItemBlockWithMetadata(Block b)
    {
        super(b);
        setHasSubtypes(true);
        ourBlock = b;
        if (b instanceof IBlockWithMetadata)
        {
            ourBlockMeta = (IBlockWithMetadata) b;
        }
    }

    @Override
    public int getMetadata (int meta)
    {
        return meta;
    }

    @Override
    public String getUnlocalizedName (ItemStack itemstack)
    {
        if (ourBlockMeta != null)
        {
            if (itemstack.getItemDamage() <= ourBlockMeta.getItemCount())
            {
                return (ourBlockMeta.getUnlocalizedNameByMetadata(itemstack.getItemDamage()));
            }
        }
        return ourBlock.getUnlocalizedName();
    }
}
