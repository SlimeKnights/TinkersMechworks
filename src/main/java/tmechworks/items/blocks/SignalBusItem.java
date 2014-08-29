package tmechworks.items.blocks;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import tmechworks.TMechworks;
import tmechworks.blocks.SignalBus;
import tmechworks.blocks.logic.SignalBusLogic;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class SignalBusItem extends ItemBlock
{
    public static final String blockType[] = { "signalbus" };
    private Block b;

    public SignalBusItem(Block b)
    {
        super(b);
        this.b = b;
        this.maxStackSize = 64;
        this.setHasSubtypes(false);
    }

    public int getMetadata (int meta)
    {
        return meta;
    }

    public String getUnlocalizedName (ItemStack itemstack)
    {
        int pos = MathHelper.clamp_int(itemstack.getItemDamage(), 0, blockType.length - 1);
        return (new StringBuilder()).append("tile.").append(blockType[pos]).toString();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean func_150936_a (World world, int x, int y, int z, int side, EntityPlayer entityPlayer, ItemStack itemStack)
    {

        if (super.func_150936_a(world, x, y, z, side, entityPlayer, itemStack) || _canPlaceItemBlockOnSide(world, x, y, z, side))
        {
            return true;
        }

        return false;
        //return super.func_150936_a(world, x, y, z, side, entityPlayer, itemStack) || _canPlaceItemBlockOnSide(world, x, y, z, side);
    }

    @Override
    public boolean onItemUse (ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ)
    {
        int tmpX = x;
        int tmpY = y;
        int tmpZ = z;

        switch (side)
        {
        case 0:
            tmpY += -1;
            break;
        case 1:
            tmpY += 1;
            break;
        case 2:
            tmpZ += -1;
            break;
        case 3:
            tmpZ += 1;
            break;
        case 4:
            tmpX += -1;
            break;
        case 5:
            tmpX += 1;
            break;
        default:
            break;
        }

        int tside = side;
        switch (side)
        {
        case 0: // DOWN
        case 1: // UP
        case 2: // NORTH
        case 3: // SOUTH
        case 4: // EAST
        case 5: // WEST
            tside = ForgeDirection.OPPOSITES[side];
            break;
        default:
            tside = side;
            break;
        }

        NBTTagCompound data = new NBTTagCompound();
        stack.stackTagCompound = data;
        data.setInteger("connectedSide", tside);

        if (super.onItemUse(stack, player, world, x, y, z, side, hitX, hitY, hitZ))
        {
            stack.stackTagCompound = null;
            return true;
        }

        if (!(_canPlaceItemBlockOnSide(world, x, y, z, side)))
        {
            return false;
        }

        TileEntity te = world.getTileEntity(tmpX, tmpY, tmpZ);

        ((SignalBusLogic) te).addPlacedSide(tside);

        stack.stackTagCompound = null;

        --stack.stackSize;

        world.func_147479_m(x, y, z);

        return true;

    }

    private boolean _canPlaceItemBlockOnSide (World world, int x, int y, int z, int side)
    {
        Block block = world.getBlock(x, y, z);

        if (block == Blocks.snow && (world.getBlockMetadata(x, y, z) & 7) < 1)
        {
            side = 1;
        }
        else if (block != Blocks.vine && block != Blocks.tallgrass && block != Blocks.deadbush && !block.isReplaceable(world, x, y, z))
        {
            if (side == 0)
            {
                --y;
            }

            if (side == 1)
            {
                ++y;
            }

            if (side == 2)
            {
                --z;
            }

            if (side == 3)
            {
                ++z;
            }

            if (side == 4)
            {
                --x;
            }

            if (side == 5)
            {
                ++x;
            }
        }

        if (!TMechworks.content.signalBus.canPlaceBlockOnSide(world, x, y, z, side))
        {
            return false;
        }

        if (world.getBlock(x, y, z) == b)
        {
            TileEntity te = world.getTileEntity(x, y, z);
            if (te == null || !(te instanceof SignalBusLogic))
            {
                return false;
            }

            return ((SignalBusLogic) te).canPlaceOnSide(ForgeDirection.OPPOSITES[side]);
        }

        return false;
    }
}
