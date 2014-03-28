package tmechworks.client;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import tmechworks.blocks.logic.AdvancedDrawbridgeLogic;
import tmechworks.blocks.logic.DrawbridgeLogic;
import tmechworks.blocks.logic.DynamoLogic;
import tmechworks.client.block.DynamoSpecialRender;
import tmechworks.client.block.FilterRender;
import tmechworks.client.block.MachineRender;
import tmechworks.client.block.SignalBusRender;
import tmechworks.client.block.SignalTerminalRender;
import tmechworks.client.gui.AdvDrawbridgeGui;
import tmechworks.client.gui.DrawbridgeGui;
import tmechworks.common.CommonProxy;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;

public class ClientProxy extends CommonProxy
{

    @Override
    public Object getClientGuiElement (int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        if (ID == drawbridgeID)
            return new DrawbridgeGui(player.inventory, (DrawbridgeLogic) world.getBlockTileEntity(x, y, z), world, x, y, z);
        if (ID == advDrawbridgeID)
            return new AdvDrawbridgeGui(player, (AdvancedDrawbridgeLogic) world.getBlockTileEntity(x, y, z), world, x, y, z);
        return null;
    }

    public void registerTickHandler ()
    {
        super.registerTickHandler();
    }

    /* Registers any rendering code. */
    public void registerRenderer ()
    {
        RenderingRegistry.registerBlockHandler(new MachineRender());
        RenderingRegistry.registerBlockHandler(new SignalBusRender());
        RenderingRegistry.registerBlockHandler(new SignalTerminalRender());
        RenderingRegistry.registerBlockHandler(new FilterRender());

        ClientRegistry.bindTileEntitySpecialRenderer(DynamoLogic.class, new DynamoSpecialRender());

    }

}
