package slimeknights.tmechworks.client.model;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.model.BakedModelWrapper;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelProperty;
import slimeknights.tmechworks.api.disguisestate.DisguiseStates;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;

public class DisguiseBakedModel extends BakedModelWrapper<BakedModel> {
    public static final ModelProperty<ItemStack> DISGUISE = new ModelProperty<>();
    public static final ModelProperty<String> DISGUISE_STATE = new ModelProperty<>();

    private final Predicate<RenderType> renderTypeLookup;

    public DisguiseBakedModel(BakedModel originalModel) {
        this(originalModel, RenderType.solid());
    }

    public DisguiseBakedModel(BakedModel originalModel, RenderType defaultRenderType) {
        this(originalModel, rt -> rt == defaultRenderType);
    }

    public DisguiseBakedModel(BakedModel originalModel, Predicate<RenderType> renderTypeLookup) {
        super(originalModel);

        this.renderTypeLookup = renderTypeLookup;
    }

    @Nonnull
    @Override
    public List<BakedQuad> getQuads(BlockState state, Direction side, @Nonnull Random rand, IModelData extraData) {

        if (extraData.hasProperty(DISGUISE)) {
            ItemStack disguise = extraData.getData(DISGUISE);

            if (disguise != null && disguise.getItem() instanceof BlockItem) {
                BlockItem disguiseItem = (BlockItem) disguise.getItem();

                BlockState disguiseState = disguiseItem.getBlock().defaultBlockState();
                disguiseState = DisguiseStates.processDisguiseStates(disguiseState, extraData.getData(DISGUISE_STATE), state.getValue(BlockStateProperties.FACING));

                if (ItemBlockRenderTypes.canRenderInLayer(disguiseState, MinecraftForgeClient.getRenderType())) {
                    BakedModel model = Minecraft.getInstance().getBlockRenderer().getBlockModelShaper().getBlockModel(disguiseState);

                    // Avoid infinite recursion when setting the disguise to another disguisable block
                    if (model instanceof DisguiseBakedModel) {
                        return ((DisguiseBakedModel) model).getSuperQuads(state, side, rand, extraData);
                    }

                    return model.getQuads(disguiseState, side, rand, extraData);
                } else {
                    return Collections.emptyList();
                }
            }
        }

        return getSuperQuads(state, side, rand, extraData);
    }

    private List<BakedQuad> getSuperQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData extraData) {
        if (renderTypeLookup.test(MinecraftForgeClient.getRenderType())) {
            return super.getQuads(state, side, rand, extraData);
        } else {
            return Collections.emptyList();
        }
    }

    @Nonnull
    @Override
    public IModelData getModelData(@Nonnull BlockAndTintGetter world, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nonnull IModelData tileData) {
        return super.getModelData(world, pos, state, tileData);
    }
}
