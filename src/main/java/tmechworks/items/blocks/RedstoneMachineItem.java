package tmechworks.items.blocks;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;

public class RedstoneMachineItem extends ItemBlock
{
    public static final String blockType[] = { "drawbridge", "firestarter", "advdrawbridge", "extdrawbridge" };

    public RedstoneMachineItem(Block b)
    {
        super(b);
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
                            list.add(StatCollector.translateToLocal("tooltip.drawbridge.inventory") + "\u00a7f" + contents.getDisplayName());
                            list.add(StatCollector.translateToLocal("tooltip.drawbridge.amount") + "\u00a7f" + contents.stackSize);
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
                            list.add(StatCollector.translateToLocal("tooltip.drawbridge.slot") + i + ": \u00a7f" + contents.getDisplayName());
                        }
                    }
                }

                NBTTagCompound camoTag = stack.getTagCompound().getCompoundTag("Camoflauge");
                if (camoTag != null)
                {
                    ItemStack camo = ItemStack.loadItemStackFromNBT(camoTag);
                    if (camo != null)
                    {
                        list.add("\u00a72" + StatCollector.translateToLocal("tooltip.drawbridge.camoflauge") + "\u00a7f" + camo.getDisplayName());
                    }
                }

                if (stack.getTagCompound().hasKey("Placement"))
                {
                    String string = getDirectionString(stack.getTagCompound().getByte("Placement"));
                    list.add(StatCollector.translateToLocal("tooltip.drawbridge.direction") + string);
                }
            }
        }
        else if (meta != 1)
        {
            list.add(StatCollector.translateToLocal("tooltip.drawbridge.default"));
        }
    }

    String getDirectionString (byte key)
    {
        if (key == 0)
            return (StatCollector.translateToLocal("tooltip.drawbridge.direction.up"));
        if (key == 1)
            return (StatCollector.translateToLocal("tooltip.drawbridge.direction.right"));
        if (key == 2)
            return (StatCollector.translateToLocal("tooltip.drawbridge.direction.down"));

        return StatCollector.translateToLocal("tooltip.drawbridge.direction.left");
    }
}