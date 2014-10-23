package tmechworks.client.item;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.FMLClientHandler;
import tmechworks.client.model.DynamoModel;
import tmechworks.common.MechContent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;

public class DynamoItemRender implements IItemRenderer
{
    private static final ResourceLocation texture = new ResourceLocation("tmechworks", "textures/entity/dynamo.png");
    private final DynamoModel model = new DynamoModel();

    @Override
    public boolean handleRenderType (ItemStack itemStack, ItemRenderType type)
    {
        if (itemStack == null || itemStack.getItem() != Item.getItemFromBlock(MechContent.dynamo))
            return false;

        return true;
    }

    @Override
    public boolean shouldUseRenderHelper (ItemRenderType type, ItemStack itemStack, ItemRendererHelper helper)
    {
        if (itemStack == null || itemStack.getItem() != Item.getItemFromBlock(MechContent.dynamo))
            return false;

        return true;
    }

    @Override
    public void renderItem (ItemRenderType type, ItemStack itemStack, Object... data)
    {
        if (itemStack == null || itemStack.getItem() != Item.getItemFromBlock(MechContent.dynamo))
            return;
        
        float xPos = 0.5F;
        float zPos = 0.5F;
        if (type == ItemRenderType.ENTITY)
        {
            xPos = 0F;
            zPos = 0F;
        }
        
        GL11.glPushMatrix();
        GL11.glTranslatef(xPos, 1.0F, zPos);
        GL11.glRotatef(0.0F, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(90, 0.0F, 1.0F, 0.0F);
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(texture);
        GL11.glScalef(1.0F, -1.0F, -1.0F);
        this.model.render(0.0625F, 0.0F);
        GL11.glPopMatrix();   

    }

}
