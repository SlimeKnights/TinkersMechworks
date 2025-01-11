package slimeknights.tmechworks.common.blocks;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import slimeknights.tmechworks.common.blocks.entity.FirestarterBlockEntity;
import slimeknights.tmechworks.common.items.MechworksBlockItem;
import slimeknights.tmechworks.library.Util;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;

public class FirestarterBlock extends RedstoneMachineBlock implements IBlockItemConstruct
{
    public static final BooleanProperty EXTINGUISH = BooleanProperty.create("extinguish");

    public FirestarterBlock()
    {
        super(Material.METAL);
        registerDefaultState(defaultBlockState().setValue(EXTINGUISH, true));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(EXTINGUISH);
    }

    @Override
    public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items) {
        ItemStack extinguishStack = new ItemStack(this, 1);
        ItemStack keepLitStack = new ItemStack(this, 1);

        CompoundTag extinguish = extinguishStack.getOrCreateTag();
        extinguish.putBoolean("extinguish", true);
        CompoundTag keepLit = keepLitStack.getOrCreateTag();
        keepLit.putBoolean("extinguish", false);

        items.add(extinguishStack);
        items.add(keepLitStack);
    }

    @Override
    public void writeAdditionalItemData(BlockState state, Level worldIn, BlockPos pos, ItemStack stack) {
        super.writeAdditionalItemData(state, worldIn, pos, stack);

        CompoundTag tags = stack.getOrCreateTag();
        tags.putBoolean("extinguish", state.getValue(EXTINGUISH));
    }

    @Nonnull
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new FirestarterBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        boolean shouldExtinguish = true;
        ItemStack stack = context.getItemInHand();

        if(stack.hasTag() && stack.getTag().contains("extinguish", CompoundTag.TAG_BYTE))
            shouldExtinguish = stack.getTag().getBoolean("extinguish");

        return super.getStateForPlacement(context).setValue(EXTINGUISH, shouldExtinguish);
    }

    @Override
    public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
        if(player.isCrouching())
            return super.use(state, worldIn, pos, player, handIn, hit);

        state = state.cycle(EXTINGUISH);

        worldIn.setBlockAndUpdate(pos, state);
        worldIn.playSound(player, pos, SoundEvents.COMPARATOR_CLICK, SoundSource.BLOCKS, 0.3F, 0.55F);

        return InteractionResult.SUCCESS;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);

        boolean shouldExtinguish = true;

        if(stack.hasTag() && stack.getTag().contains("extinguish", CompoundTag.TAG_BYTE))
            shouldExtinguish = stack.getTag().getBoolean("extinguish");

        tooltip.add(Component.translatable(Util.prefix("tooltip.behaviour"), I18n.get(Util.prefix("tooltip.behaviour.firestarter." + (shouldExtinguish ? "extinguish" : "keep")))).withStyle(ChatFormatting.GRAY));
    }

    @Override
    public void setDefaultNBT(CompoundTag nbt, CompoundTag blockState) {
        // Firestarter does not have an inventory
        //        super.setDefaultNBT(nbt, blockState);

        if(!nbt.contains("extinguish"))
            nbt.putBoolean("extinguish", true);
    }

    @Override
    public void onBlockItemConstruct(MechworksBlockItem item) {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> registerItemProperties(item));
    }

    @OnlyIn(Dist.CLIENT)
    public void registerItemProperties(MechworksBlockItem item) {
        ItemProperties.register(item, new ResourceLocation("extinguish"), (stack, world, entity, seed) -> {
            boolean shouldExtinguish = true;

            if(stack.hasTag() && stack.getTag().contains("extinguish", CompoundTag.TAG_BYTE))
                shouldExtinguish = stack.getTag().getBoolean("extinguish");

            return shouldExtinguish ? 1F : 0F;
        });
    }
}
