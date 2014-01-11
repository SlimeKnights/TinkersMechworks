package tmechworks.client.gui;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.Iterator;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import org.lwjgl.opengl.GL11;

import tmechworks.blocks.logic.AdvancedDrawbridgeLogic;
import tmechworks.inventory.AdvancedDrawbridgeContainer;
import cpw.mods.fml.common.network.PacketDispatcher;

public class AdvDrawbridgeGui extends GuiContainer
{
    public AdvancedDrawbridgeLogic logic;

    public boolean isGuiExpanded = false;
    public boolean containerNeglectMouse = false;

    public AdvDrawbridgeGui(EntityPlayer player, AdvancedDrawbridgeLogic frypan, World world, int x, int y, int z)
    {
        super(frypan.getGuiContainer(player.inventory, world, x, y, z));
        this.inventorySlots = new AdvancedDrawbridgeContainer(player.inventory, frypan, this);
        player.openContainer = this.inventorySlots;
        logic = frypan;
    }

    protected void drawGuiContainerForegroundLayer (int par1, int par2)
    {
        field_146289_q.drawString("Advanced Drawbridge", 8, 6, 0x404040);
        field_146289_q.drawString(StatCollector.translateToLocal("container.inventory"), 8, (ySize - 96) + 2, 0x404040);
    }

    private static final ResourceLocation background = new ResourceLocation("tmechworks", "textures/gui/drawbridgeAdvanced.png");

    protected void drawGuiContainerBackgroundLayer (float f, int i, int j)
    {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.field_146297_k.getTextureManager().bindTexture(background);
        int cornerX = (field_146294_l - xSize) / 2;
        int cornerY = (field_146295_m - ySize) / 2;
        drawTexturedModalRect(cornerX, cornerY, 0, 0, xSize, ySize);
        if (!isGuiExpanded)
        {
            drawTexturedModalRect(cornerX + 34, cornerY + 35, 238, 0, 18, 18);
        }
        else
        {
            for (int index = 0; index < 16; index++)
            {
                drawTexturedModalRect(index < 8 ? cornerX + 9 + 20 * index : cornerX + 9 + 20 * (index - 8), cornerY + 34 + (int) Math.floor(index / 8) * 18 + (index < 8 ? 0 : 1), 238, 0, 18, 18);
                if (index != 15)
                {
                    drawTexturedModalRect(index < 8 ? cornerX + 26 + 20 * index : cornerX + 26 + 20 * (index - 8), cornerY + 34 + (int) Math.floor(index / 8) * 18 + (index < 8 ? 0 : 1) + 6, 253, 18,
                            3, 5);
                }
            }
            //        	drawTexturedModalRect(cornerX - 7, cornerY + 29, 0, 167, 10, 40);
            //        	drawTexturedModalRect(cornerX - 7, cornerY + 29, 0, 167, 10, 40);
            //        	drawTexturedModalRect(cornerX + 173, cornerY + 29, 10, 167, 10, 40);
        }
    }

    public void initGui ()
    {
        super.initGui();
        int cornerX = (this.field_146294_l - this.xSize) / 2;
        int cornerY = (this.field_146295_m - this.ySize) / 2;

        this.setExpanded(false);
        this.field_146292_n.clear();
        GuiButton button = new DrawbridgeButton(0, cornerX + 131, cornerY + 18, 176, 0, 21, 22);
        if (logic.getPlacementDirection() == 0)
            button.field_146124_l = false;
        this.field_146292_n.add(button);
        button = new DrawbridgeButton(1, cornerX + 146, cornerY + 34, 199, 23, 22, 21);
        if (logic.getPlacementDirection() == 1)
            button.field_146124_l = false;
        this.field_146292_n.add(button);
        button = new DrawbridgeButton(2, cornerX + 132, cornerY + 48, 199, 0, 21, 22);
        if (logic.getPlacementDirection() == 2)
            button.field_146124_l = false;
        this.field_146292_n.add(button);
        button = new DrawbridgeButton(3, cornerX + 117, cornerY + 34, 178, 23, 22, 21);
        if (logic.getPlacementDirection() == 3)
            button.field_146124_l = false;
        this.field_146292_n.add(button);
        button = new DrawbridgeButton(4, cornerX + 135, cornerY + 40, 217, 0, 10, 10);
        if (logic.getPlacementDirection() == 4)
            button.field_146124_l = false;
        this.field_146292_n.add(button);

        this.field_146292_n.add(new AdvDrawbridgeButton(5, this.field_146294_l / 2 - 13, this.field_146295_m / 2 - 52, this.field_146294_l / 2 + 58, this.field_146295_m / 2 - 79, 26, 26, "Inv"));
    }

    public void setExpanded (boolean flag)
    {
        this.isGuiExpanded = flag;
        this.containerNeglectMouse = true;
        Iterator<GuiButton> i1 = this.field_146292_n.iterator();
        while (i1.hasNext())
        {
            GuiButton b = i1.next();
            if (b instanceof AdvDrawbridgeButton)
            {
                ((AdvDrawbridgeButton) b).isGuiExpanded = flag;
            }
            else
            {
                b.field_146125_m = !flag;
            }
        }
        ((AdvancedDrawbridgeContainer) this.inventorySlots).updateContainerSlots();
    }

    protected void actionPerformed (GuiButton button)
    {
        if (button.field_146127_k == 5)
        {
            setExpanded(!isGuiExpanded);
            return;
        }

        for (Object o : field_146292_n)
        {
            GuiButton b = (GuiButton) o;
            b.field_146124_l = true;
        }
        button.field_146124_l = false;

       //logic.setPlacementDirection((byte) button.id);
        updateServer((byte) button.field_146127_k);
    }

    void updateServer (byte direction)
    {
        ByteArrayOutputStream bos = new ByteArrayOutputStream(8);
        DataOutputStream outputStream = new DataOutputStream(bos);
        try
        {
            outputStream.writeByte(5);
            outputStream.writeInt(logic.getWorld().provider.dimensionId);
            outputStream.writeInt(logic.field_145851_c);
            outputStream.writeInt(logic.field_145848_d);
            outputStream.writeInt(logic.field_145849_e);
            outputStream.writeByte(direction);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        Packet250CustomPayload packet = new Packet250CustomPayload();
        packet.channel = "TMechworks";
        packet.data = bos.toByteArray();
        packet.length = bos.size();

        PacketDispatcher.sendPacketToServer(packet);
    }

    @Override
    public void updateScreen ()
    {
        super.updateScreen();
        this.containerNeglectMouse = false;
    }
}
