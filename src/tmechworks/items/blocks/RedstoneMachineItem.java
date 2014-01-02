package tmechworks.items.blocks;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;

public class RedstoneMachineItem extends ItemBlock
{
    public static final String blockType[] = { "drawbridge", "firestarter", "advdrawbridge", "extdrawbridge" };

    public RedstoneMachineItem(int id)
    {
        super(id);
        setMaxDamage(0);
        setHasSubtypes(true);
    }

    public int getMetadata (int meta)
    {
        return meta;
    }

    public String getUnlocalizedName (ItemStack itemstack)
    {
        int pos = MathHelper.clamp_int(itemstack.getItemDamage(), 0, blockType.length - 1);
        return (new StringBuilder()).append("block.").append(blockType[pos]).toString();
    }

    @Override
    public void addInformation (ItemStack stack, EntityPlayer player, List list, boolean par4)
    {
        int meta = stack.getItemDamage();
        if (stack.hasTagCompound())
        {
            if (meta != 1)
            {
                if (meta == 0 || meta == 3)
                {
                    NBTTagCompound contentTags = stack.getTagCompound().getCompoundTag("Contents");
                    if (contentTags != null)
                    {
                        ItemStack contents = ItemStack.loadItemStackFromNBT(contentTags);
                        if (contents != null)
                        {
                            list.add("Inventory: \u00a7f" + contents.getDisplayName());
                            list.add("Amount: \u00a7f" + contents.stackSize);
                        }
                    }
                }
                else if (meta == 2)
                {
                    for (int i = 1; i <= 16; i++)
                    {
                        NBTTagCompound contentTag = stack.getTagCompound().getCompoundTag("Slot" + i);
                        ItemStack contents = ItemStack.loadItemStackFromNBT(contentTag);
                        if (contents != null)
                        {
                            list.add("Slot " + i + ": \u00a7f" + contents.getDisplayName());
                        }
                    }
                }

                NBTTagCompound camoTag = stack.getTagCompound().getCompoundTag("Camoflauge");
                if (camoTag != null)
                {
                    ItemStack camo = ItemStack.loadItemStackFromNBT(camoTag);
                    if (camo != null)
                    {
                        list.add("\u00a72Camoflauge: \u00a7f" + camo.getDisplayName());
                    }
                }

                if (stack.getTagCompound().hasKey("Placement"))
                {
                    String string = getDirectionString(stack.getTagCompound().getByte("Placement"));
                    list.add("Placement Direction: " + string);
                }
            }
        }
        else if (meta != 1)
        {
            list.add("Stores its inventory when harvested");
        }
    }

    String getDirectionString (byte key)
    {
        if (key == 0)
            return ("Up");
        if (key == 1)
            return ("Right");
        if (key == 2)
            return ("Down");

        return "Left";
    }
}
