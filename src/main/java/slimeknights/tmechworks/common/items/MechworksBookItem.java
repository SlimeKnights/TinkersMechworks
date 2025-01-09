package slimeknights.tmechworks.common.items;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import slimeknights.mantle.item.LecternBookItem;
import slimeknights.tmechworks.client.ClientProxy;
import slimeknights.tmechworks.common.MechworksContent;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

public class MechworksBookItem extends LecternBookItem {
    public MechworksBookItem() {
        super(new Item.Properties().tab(MechworksContent.tabMechworks).stacksTo(1));
    }

    @Override
    @Nonnull
    @ParametersAreNonnullByDefault
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand hand) {
        ItemStack stack = playerIn.getItemInHand(hand);

        if(worldIn.isClientSide){
            ClientProxy.book.openGui(hand, stack);
        }

        return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);
    }

    @Override
    @ParametersAreNonnullByDefault
    public void openLecternScreenClient(BlockPos pos, ItemStack stack) {
        ClientProxy.book.openGui(pos, stack);
    }
}
