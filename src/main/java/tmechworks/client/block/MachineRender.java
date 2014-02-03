package tmechworks.client.block;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.world.IBlockAccess;
import tconstruct.client.TProxyClient;
import mantle.blocks.iface.IFacingLogic;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;

public class MachineRender implements ISimpleBlockRenderingHandler
{
    public static int model = RenderingRegistry.getNextAvailableRenderId();

    @Override
    public void renderInventoryBlock (Block block, int metadata, int modelID, RenderBlocks renderer)
    {
        if (modelID == model)
        {
            TProxyClient.renderStandardInvBlock(renderer, block, metadata);
        }
    }

    @Override
    public boolean renderWorldBlock (IBlockAccess world, int x, int y, int z, Block block, int modelID, RenderBlocks renderer)
    {
        if (modelID == model)
        {
            int metadata = world.getBlockMetadata(x, y, z);
            if (metadata != 1)
            {
                renderer.func_147784_q(block, x, y, z);
            }
            else
            {
                renderRotatedBlock(block, x, y, z, world, renderer);
            }
        }
        return true;
    }

    public boolean renderRotatedBlock (Block block, int x, int y, int z, IBlockAccess world, RenderBlocks renderer)
    {
        IFacingLogic logic = (IFacingLogic) world.func_147438_o(x, y, z);
        byte direction = logic.getRenderDirection();

        if (direction == 0)
        {
            renderer.field_147875_q = 3;
            renderer.field_147873_r = 3;
            renderer.field_147869_t = 3;
            renderer.field_147871_s = 3;
        }
        if (direction == 2)
        {
            renderer.field_147869_t = 2;
            renderer.field_147871_s = 1;
        }
        if (direction == 3)
        {
            renderer.field_147869_t = 1;
            renderer.field_147871_s = 2;
            renderer.field_147867_u = 3;
            renderer.field_147865_v = 3;
        }
        if (direction == 4)
        {
            renderer.field_147875_q = 1;
            renderer.field_147873_r = 2;
            renderer.field_147867_u = 2;
            renderer.field_147865_v = 1;
        }
        if (direction == 5)
        {
            renderer.field_147875_q = 2;
            renderer.field_147873_r = 1;
            renderer.field_147867_u = 1;
            renderer.field_147865_v = 2;
        }

        boolean flag = renderer.func_147784_q(block, x, y, z);
        renderer.field_147871_s = 0;
        renderer.field_147875_q = 0;
        renderer.field_147873_r = 0;
        renderer.field_147869_t = 0;
        renderer.field_147867_u = 0;
        renderer.field_147865_v = 0;
        return flag;
    }

    @Override
    public boolean shouldRender3DInInventory (int modelID)
    {
        return true;
    }

    @Override
    public int getRenderId ()
    {
        return model;
    }
}
