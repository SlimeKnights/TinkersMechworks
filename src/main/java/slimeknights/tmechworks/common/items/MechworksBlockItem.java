package slimeknights.tmechworks.common.items;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import slimeknights.mantle.item.BlockTooltipItem;
import slimeknights.tmechworks.common.blocks.IBlockItemConstruct;
import slimeknights.tmechworks.common.blocks.RedstoneMachineBlock;

import javax.annotation.Nullable;

public class MechworksBlockItem extends BlockTooltipItem {
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

        nbt = stack.getOrCreateTag();

        CompoundNBT tags = new CompoundNBT();
        ((RedstoneMachineBlock)block).setDefaultNBT(nbt, tags);

        if(!nbt.contains("BlockEntityTag"))
            nbt.put("BlockEntityTag", tags);

        stack.setTag(nbt);

        return null;
    }
}
