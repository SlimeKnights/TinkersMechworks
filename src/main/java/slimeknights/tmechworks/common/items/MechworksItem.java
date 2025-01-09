package slimeknights.tmechworks.common.items;

import net.minecraft.client.resources.language.I18n;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import slimeknights.tmechworks.common.MechworksContent;
import slimeknights.tmechworks.library.TranslationUtil;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Supplier;

public class MechworksItem extends Item {
    private Object[] tooltipFormat;
    private Supplier<Object[]> tooltipFormatSupplier = () -> tooltipFormat;

    public MechworksItem() {
        this(new Properties());
    }

    public MechworksItem(Properties properties) {
        super(properties.tab(MechworksContent.tabMechworks));
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
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flag) {
        if (I18n.exists(getDescriptionId(stack) + ".tooltip")) {
            tooltip.addAll(TranslationUtil.getTooltips(I18n.get(getDescriptionId(stack) + ".tooltip", tooltipFormatSupplier.get())));
        }
    }
}
