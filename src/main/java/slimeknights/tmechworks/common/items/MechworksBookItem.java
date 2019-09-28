package slimeknights.tmechworks.common.items;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import slimeknights.mantle.client.book.BookLoader;
import slimeknights.mantle.client.book.data.BookData;
import slimeknights.mantle.client.book.repository.FileRepository;
import slimeknights.tmechworks.client.ClientProxy;
import slimeknights.tmechworks.common.MechworksContent;

import javax.annotation.Nonnull;

public class MechworksBookItem extends MechworksItem {
    public MechworksBookItem() {
        super(new Item.Properties().maxStackSize(1));
    }

    @Nonnull
    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack stack = playerIn.getHeldItem(handIn);

        if(worldIn.isRemote){
            ClientProxy.book.openGui(new TranslationTextComponent("item.tmechworks.book"), stack);
        }

        return new ActionResult<>(ActionResultType.SUCCESS, stack);
    }
}
