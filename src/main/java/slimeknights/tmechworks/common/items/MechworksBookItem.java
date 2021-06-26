package slimeknights.tmechworks.common.items;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import slimeknights.mantle.item.LecternBookItem;
import slimeknights.tmechworks.client.ClientProxy;
import slimeknights.tmechworks.common.MechworksContent;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

public class MechworksBookItem extends LecternBookItem {
    public MechworksBookItem() {
        super(new Item.Properties().group(MechworksContent.tabMechworks).maxStackSize(1));
    }

    @Override
    @Nonnull
    @ParametersAreNonnullByDefault
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand hand) {
        ItemStack stack = playerIn.getHeldItem(hand);

        if(worldIn.isRemote){
            ClientProxy.book.openGui(hand, stack);
        }

        return new ActionResult<>(ActionResultType.SUCCESS, stack);
    }

    @Override
    @ParametersAreNonnullByDefault
    public void openLecternScreenClient(BlockPos pos, ItemStack stack) {
        ClientProxy.book.openGui(pos, stack);
    }
}
