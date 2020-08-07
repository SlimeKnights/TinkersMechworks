package slimeknights.tmechworks.common.items;

import net.minecraft.block.Block;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import slimeknights.tmechworks.common.blocks.IBlockItemConstruct;
import slimeknights.tmechworks.common.blocks.RedstoneMachineBlock;
import slimeknights.tmechworks.library.TranslationUtil;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class MechworksBlockItem extends BlockItem {
    private Object[] tooltipFormat;
    private Supplier<Object[]> tooltipFormatSupplier = () -> tooltipFormat;

    public MechworksBlockItem(Block blockIn, Properties builder) {
        super(blockIn, builder);

        if(blockIn instanceof IBlockItemConstruct)
            ((IBlockItemConstruct)blockIn).onBlockItemConstruct(this);
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
        Block block = getBlock();

        if(!(block instanceof RedstoneMachineBlock))
            return super.initCapabilities(stack, nbt);

        nbt = stack.getTag();

        if(nbt == null)
            nbt = new CompoundNBT();

        CompoundNBT tags = new CompoundNBT();
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
    public void addInformation(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flag) {
        if (I18n.hasKey(getTranslationKey(stack) + ".tooltip")) {
            tooltip.addAll(TranslationUtil.getTooltips(I18n.format(getTranslationKey(stack) + ".tooltip", tooltipFormatSupplier.get())));
        }

        super.addInformation(stack, world, tooltip, flag);
    }
}
