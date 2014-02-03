package tmechworks.blocks.logic;

import mantle.blocks.iface.*;
import mantle.world.WorldHelper;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.*;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraftforge.common.util.ForgeDirection;
import tconstruct.library.tools.AbilityHelper;

public class FirestarterLogic extends TileEntity implements IFacingLogic, IActiveLogic
{
    boolean active;
    //boolean putOut;
    boolean shouldActivate;
    byte direction;

    @Override
    public boolean getActive ()
    {
        return active;
    }

    @Override
    public void setActive (boolean flag)
    {
        /*if (active && !flag)
        {
            putOut = true;
        }*/

        active = flag;
        shouldActivate = true;
        //setFire();
    }

    void setFire ()
    {
        int xPos = field_145851_c;
        int yPos = field_145848_d;
        int zPos = field_145849_e;

        switch (direction)
        {
        case 0:
            yPos -= 1;
            break;
        case 1:
            yPos += 1;
            break;
        case 2:
            zPos -= 1;
            break;
        case 3:
            zPos += 1;
            break;
        case 4:
            xPos -= 1;
            break;
        case 5:
            xPos += 1;
            break;
        }

        Block block = field_145850_b.func_147439_a(xPos, yPos, zPos);
        if (active)
        {
//            TMechworks.logger.info("Setting fire");
            if (block == null || WorldHelper.isAirBlock(field_145850_b, xPos, yPos, zPos))
            {
                field_145850_b.playSoundEffect((double) xPos + 0.5D, (double) yPos + 0.5D, (double) zPos + 0.5D, "fire.ignite", 1.0F, AbilityHelper.random.nextFloat() * 0.4F + 0.8F);
                field_145850_b.func_147449_b(xPos, yPos, zPos, Blocks.fire);
            }
        }
        else
        {
            //TConstruct.logger.info("Stopping fire "+putOut);
            if (block == Blocks.fire)
            {
                //field_145850_b.playSoundEffect((double) xPos + 0.5D, (double) yPos + 0.5D, (double) zPos + 0.5D, "random.fizz", 1.0F, AbilityHelper.random.nextFloat() * 0.4F + 0.8F);
                field_145850_b.playSoundEffect((double) xPos + 0.5D, (double) yPos + 0.5D, (double) zPos + 0.5D, "fire.ignite", 1.0F, AbilityHelper.random.nextFloat() * 0.4F + 0.8F);
                field_145850_b.func_147465_d(xPos, yPos, zPos, Blocks.air, 0, 3);
                //putOut = false;
                shouldActivate = true;
            }
        }
    }

    public void updateEntity ()
    {
        if (shouldActivate)
        {
            shouldActivate = false;
            setFire();
        }
    }

    @Override
    public byte getRenderDirection ()
    {
        return direction;
    }

    @Override
    public ForgeDirection getForgeDirection ()
    {
        return ForgeDirection.VALID_DIRECTIONS[direction];
    }

    @Override
    public void setDirection (int side)
    {
    }

    @Override
    public void setDirection (float yaw, float pitch, EntityLivingBase player)
    {
        if (pitch > 45)
        {
            direction = 1;
        }
        else if (pitch < -45)
        {
            direction = 0;
        }
        else
        {
            int facing = MathHelper.floor_double((double) (yaw / 360) + 0.5D) & 3;
            switch (facing)
            {
            case 0:
                direction = 2;
                break;

            case 1:
                direction = 5;
                break;

            case 2:
                direction = 3;
                break;

            case 3:
                direction = 4;
                break;
            }
        }
    }

    @Override
    public void func_145839_a (NBTTagCompound tags)
    {
        super.func_145839_a(tags);
        tags.setBoolean("Active", active);
        readCustomNBT(tags);
    }

    @Override
    public void func_145841_b (NBTTagCompound tags)
    {
        super.func_145841_b(tags);
        active = tags.getBoolean("Active");
        writeCustomNBT(tags);
    }

    public void readCustomNBT (NBTTagCompound tags)
    {
        direction = tags.getByte("Direction");
    }

    public void writeCustomNBT (NBTTagCompound tags)
    {
        tags.setByte("Direction", direction);
    }

    /* Packets */
    @Override
    public Packet func_145844_m ()
    {
        NBTTagCompound tag = new NBTTagCompound();
        writeCustomNBT(tag);
        return new S35PacketUpdateTileEntity(field_145851_c, field_145848_d, field_145849_e, 1, tag);
    }

    @Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity packet)
    {
        readCustomNBT(packet.func_148857_g());
        field_145850_b.func_147479_m(field_145851_c, field_145848_d, field_145849_e);
    }
}
