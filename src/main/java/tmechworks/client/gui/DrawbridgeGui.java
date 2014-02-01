package tmechworks.client.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.*;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;
import tmechworks.TMechworks;
import tmechworks.blocks.logic.DrawbridgeLogic;
import tmechworks.network.packet.PacketDrawbridge;

public class DrawbridgeGui extends GuiContainer
{
    public DrawbridgeLogic logic;

    public DrawbridgeGui(InventoryPlayer inventoryplayer, DrawbridgeLogic frypan, World world, int x, int y, int z)
    {
        super(frypan.getGuiContainer(inventoryplayer, world, x, y, z));
        logic = frypan;
    }

    @Override
    protected void drawGuiContainerForegroundLayer (int par1, int par2)
    {
        //fontRenderer.drawString(StatCollector.translateToLocal("aggregator.glowstone"), 60, 6, 0x404040);
        field_146289_q.drawString("Drawbridge", 8, 6, 0x404040);
        field_146289_q.drawString(StatCollector.translateToLocal("container.inventory"), 8, (ySize - 96) + 2, 0x404040);
    }

    private static final ResourceLocation background = new ResourceLocation("tmechworks", "textures/gui/drawbridge.png");

    @Override
    protected void drawGuiContainerBackgroundLayer (float f, int i, int j)
    {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.field_146297_k.getTextureManager().bindTexture(background);
        int cornerX = (field_146294_l - xSize) / 2;
        int cornerY = (field_146295_m - ySize) / 2;
        drawTexturedModalRect(cornerX, cornerY, 0, 0, xSize, ySize);
    }

    public void initGui ()
    {
        super.initGui();
        int cornerX = (this.field_146294_l - this.xSize) / 2;
        int cornerY = (this.field_146295_m - this.ySize) / 2;

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
    }

    protected void actionPerformed (GuiButton button)
    {
        for (Object o : field_146292_n)
        {
            GuiButton b = (GuiButton) o;
            b.field_146124_l = true;
        }
        button.field_146124_l = false;

        //logic.setPlacementDirection((byte) button.field_146127_k);
        updateServer((byte) button.field_146127_k);
    }

    void updateServer (byte direction)
    {
        /*ByteArrayOutputStream bos = new ByteArrayOutputStream(8);
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

        PacketDispatcher.sendPacketToServer(packet);*/
        
        TMechworks.packetPipeline.sendToServer(new PacketDrawbridge(logic.field_145851_c, logic.field_145848_d, logic.field_145849_e, direction));
    }
}
