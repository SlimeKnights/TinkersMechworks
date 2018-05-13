package slimeknights.tmechworks.blocks.logic;

import net.minecraft.item.ItemStack;

public interface IDisguisable
{

    ItemStack getDisguiseBlock ();

    void setDisguiseBlock (ItemStack disguise);

    void markDirtyD ();

    boolean canEditDisguise ();
}
