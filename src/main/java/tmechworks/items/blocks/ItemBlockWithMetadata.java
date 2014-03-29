package tmechworks.items.blocks;

import java.util.List;

import tmechworks.lib.blocks.IBlockWithMetadata;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;

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
