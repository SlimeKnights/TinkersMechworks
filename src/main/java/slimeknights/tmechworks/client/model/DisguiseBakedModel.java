package slimeknights.tmechworks.client.model;

import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraftforge.client.ChunkRenderTypeSet;
import net.minecraftforge.client.model.BakedModelWrapper;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.data.ModelProperty;
import slimeknights.tmechworks.api.disguisestate.DisguiseStates;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class DisguiseBakedModel extends BakedModelWrapper<BakedModel> {
    public static final ModelProperty<ItemStack> DISGUISE = new ModelProperty<>();
    public static final ModelProperty<String> DISGUISE_STATE = new ModelProperty<>();

    private final ChunkRenderTypeSet defaultRenderTypes;

    public DisguiseBakedModel(BakedModel originalModel) {
        this(originalModel, RenderType.solid());
    }

    public DisguiseBakedModel(BakedModel originalModel, RenderType defaultRenderType) {
        this(originalModel, ChunkRenderTypeSet.of(defaultRenderType));
    }

    public DisguiseBakedModel(BakedModel originalModel, ChunkRenderTypeSet defaultRenderTypes) {
        super(originalModel);

        this.defaultRenderTypes = defaultRenderTypes;
    }

    @Nonnull
    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource rand, ModelData extraData, @Nullable RenderType renderType) {
        BlockState disguiseState = getDisguiseState(state, extraData);

        if (disguiseState != null) {
            BakedModel model = Minecraft.getInstance().getBlockRenderer().getBlockModelShaper().getBlockModel(disguiseState);
            if (model instanceof DisguiseBakedModel) {
                return ((DisguiseBakedModel) model).getSuperQuads(state, side, rand, extraData, renderType);
            }

            return model.getQuads(disguiseState, side, rand, extraData, renderType);
        } else {
            return getSuperQuads(state, side, rand, extraData, renderType);
        }
    }

    private List<BakedQuad> getSuperQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull RandomSource rand, @Nonnull ModelData extraData, RenderType renderType) {
        return super.getQuads(state, side, rand, extraData, renderType);
    }

    @Nonnull
    @Override
    public ModelData getModelData(@Nonnull BlockAndTintGetter world, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nonnull ModelData tileData) {
        return super.getModelData(world, pos, state, tileData);
    }

    @Nullable
    public BlockState getDisguiseState(@Nullable BlockState state, ModelData extraData) {
        if (extraData.has(DISGUISE)) {
            ItemStack disguise = extraData.get(DISGUISE);

            if (disguise != null && disguise.getItem() instanceof BlockItem disguiseItem) {
                BlockState disguiseState = disguiseItem.getBlock().defaultBlockState();
                disguiseState = DisguiseStates.processDisguiseStates(disguiseState, extraData.get(DISGUISE_STATE), state.getValue(BlockStateProperties.FACING));

                return disguiseState;
            }
        }

        return null;
    }

    @Override
    public ChunkRenderTypeSet getRenderTypes(BlockState state, RandomSource rand, ModelData data) {
        BlockState disguiseState = getDisguiseState(state, data);
        if (disguiseState != null) {
            BakedModel model = Minecraft.getInstance().getBlockRenderer().getBlockModelShaper().getBlockModel(disguiseState);
            if (model instanceof DisguiseBakedModel) {
                return ((DisguiseBakedModel) model).getSuperRenderTypes(state, rand, data);
            }

            return model.getRenderTypes(state, rand, data);
        } else {
            return defaultRenderTypes;
        }
    }

    private ChunkRenderTypeSet getSuperRenderTypes(BlockState state, RandomSource rand, ModelData data) {
        return super.getRenderTypes(state, rand, data);
    }
}
