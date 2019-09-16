package slimeknights.tmechworks.common.items;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import slimeknights.mantle.util.LocUtils;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class MechworksItem extends Item {
    private Object[] tooltipFormat;
    private Supplier<Object[]> tooltipFormatSupplier = () -> tooltipFormat;

    public MechworksItem(Properties properties) {
        super(properties);
    }

    public MechworksItem setTooltipFormat(Object... format){
        this.tooltipFormat = format;

        return this;
    }

    public MechworksItem setTooltipFormatSupplier(Supplier<Object[]> formatSupplier){
        tooltipFormatSupplier = formatSupplier;

        return this;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flag) {
        if (I18n.hasKey(getTranslationKey(stack) + ".tooltip")) {
            tooltip.addAll(LocUtils.getTooltips(I18n.format(getTranslationKey(stack) + ".tooltip", tooltipFormatSupplier.get())).stream().map(x -> x.applyTextStyle(TextFormatting.GRAY)).collect(Collectors.toList()));
        }
    }
}
