package slimeknights.tmechworks.common.items;

import net.minecraft.world.level.block.Block;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import slimeknights.tmechworks.common.blocks.IBlockItemConstruct;
import slimeknights.tmechworks.common.blocks.RedstoneMachineBlock;
import slimeknights.tmechworks.library.TranslationUtil;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Supplier;

public class MechworksBlockItem extends BlockItem {
    private Object[] tooltipFormat;
    private Supplier<Object[]> tooltipFormatSupplier = () -> tooltipFormat;

    public MechworksBlockItem(Block blockIn, Properties builder) {
        super(blockIn, builder);

        if(blockIn instanceof IBlockItemConstruct) {
            ((IBlockItemConstruct) blockIn).onBlockItemConstruct(this);
        }
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        Block block = getBlock();

        if(!(block instanceof RedstoneMachineBlock))
            return super.initCapabilities(stack, nbt);

        nbt = stack.getTag();

        if(nbt == null)
            nbt = new CompoundTag();

        CompoundTag tags = new CompoundTag();
        ((RedstoneMachineBlock)block).setDefaultNBT(nbt, tags);

        if(!tags.isEmpty() && !nbt.contains("BlockEntityTag"))
            nbt.put("BlockEntityTag", tags);
        if(!nbt.isEmpty())
            stack.setTag(nbt);

        return null;
    }

    public MechworksBlockItem setTooltipFormat(Object... format){
        this.tooltipFormat = format;

        return this;
    }

    public MechworksBlockItem setTooltipFormatSupplier(Supplier<Object[]> formatSupplier){
        tooltipFormatSupplier = formatSupplier;

        return this;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flag) {
        if (I18n.exists(getDescriptionId(stack) + ".tooltip")) {
            tooltip.addAll(TranslationUtil.getTooltips(I18n.get(getDescriptionId(stack) + ".tooltip", tooltipFormatSupplier.get())));
        }

        super.appendHoverText(stack, world, tooltip, flag);
    }
}
