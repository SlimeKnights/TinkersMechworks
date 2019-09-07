package slimeknights.tmechworks.common.items;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import slimeknights.mantle.client.book.BookLoader;
import slimeknights.mantle.client.book.data.BookData;
import slimeknights.mantle.client.book.repository.FileRepository;
import slimeknights.mantle.util.LocUtils;
import slimeknights.tmechworks.common.TMechContent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.Collectors;

public class MechworksBookItem extends Item {
    private static final BookData book = BookLoader.registerBook("tmechworks:book", true, false, new FileRepository("tmechworks:book"));

    public MechworksBookItem() {
        super(new Item.Properties().group(TMechContent.tabMechworks).maxStackSize(1));
    }

    @Nonnull
    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack stack = playerIn.getHeldItem(handIn);

        if(worldIn.isRemote){
            book.openGui(new TranslationTextComponent("item.tmechworks.book"), stack);
        }

        return new ActionResult<>(ActionResultType.SUCCESS, stack);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        tooltip.addAll(LocUtils.getTooltips(TextFormatting.GRAY.toString() + I18n.format(super.getTranslationKey(stack) + ".tooltip")).stream().map(x->x.applyTextStyle(TextFormatting.GRAY)).collect(Collectors.toList()));
    }
}
