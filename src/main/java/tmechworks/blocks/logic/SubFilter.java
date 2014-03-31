package tmechworks.blocks.logic;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;

public abstract class SubFilter
{
    //The item that is this SubFilter mesh: the item that, if you put it in the frame, is this SubFilter.
    protected ItemStack associatedItem;
    protected String meshIcon = null;
    protected boolean metaSensitive = true;

    protected String suffix = "unnamedfilter";

    public String getSuffix ()
    {
        return suffix;
    }

    public void setSuffix (String suffix)
    {
        this.suffix = suffix;
    }

    public String getMeshIconName ()
    {

        return meshIcon;
    }

    public void setMeshIconName (String m)
    {
        meshIcon = m;
    }

    public abstract boolean canPass (Entity entity);

    public abstract boolean canPass (ItemStack itemStack);

    public ItemStack getAssociatedItem ()
    {
        return associatedItem;
    }

    public void setAssociatedItem (ItemStack associatedItem)
    {
        this.associatedItem = associatedItem;
    }

    public boolean isItemMetaSensitive ()
    {
        return metaSensitive;
    }

    public void setItemMetaSensitive (boolean metaSensitive)
    {
        this.metaSensitive = metaSensitive;
    }
}
