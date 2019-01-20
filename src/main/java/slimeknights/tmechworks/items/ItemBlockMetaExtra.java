package slimeknights.tmechworks.items;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.StringUtils;
import slimeknights.mantle.item.ItemBlockMeta;
import slimeknights.tmechworks.blocks.RedstoneMachine;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collection;

public class ItemBlockMetaExtra extends ItemBlockMeta {
    private String extra = "";

    public ItemBlockMetaExtra(Block block, String... extraData) {
        super(block);

        if (extraData.length == 0) {
            this.extra = "facing=inv";
        } else {
            Arrays.sort(extraData);
            this.extra = StringUtils.join(extraData, ',');
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerItemModels() {
        final Item item = this;
        final ResourceLocation loc = block.getRegistryName();


        for (Comparable o : (Collection<Comparable>) mappingProperty.getAllowedValues()) {
            int meta = block.getMetaFromState(block.getDefaultState().withProperty(mappingProperty, o));
            String name = mappingProperty.getName(o);

            String[] props = (extra + "," + mappingProperty.getName() + "=" + name).split(",");
            Arrays.sort(props);

            ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(loc, StringUtils.join(props, ',')));
        }
    }

    @Nullable
    @Override
    public net.minecraftforge.common.capabilities.ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt) {
        Block block = getBlock();

        if(!(block instanceof RedstoneMachine))
            return super.initCapabilities(stack, nbt);

        if(nbt == null) {
            nbt = new NBTTagCompound();
            stack.setTagCompound(nbt);
        } else if(nbt.hasKey("BlockEntityTag"))
            return null;

        NBTTagCompound tags = new NBTTagCompound();

        ((RedstoneMachine)block).setDefaultNBT(tags);
        nbt.setTag("BlockEntityTag", tags);

        return null;
    }
}
