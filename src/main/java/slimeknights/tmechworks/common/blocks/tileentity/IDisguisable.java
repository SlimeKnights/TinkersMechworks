package slimeknights.tmechworks.common.blocks.tileentity;

import net.minecraft.item.ItemStack;

public interface IDisguisable
{
    ItemStack getDisguiseBlock ();

    void setDisguiseBlock (ItemStack disguise);

    void markDirty ();

    boolean canEditDisguise ();
}
