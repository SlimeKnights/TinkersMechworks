package slimeknights.tmechworks.client.model;

import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockDisplayReader;
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

public class DisguiseBakedModel extends BakedModelWrapper<IBakedModel> {
    public static final ModelProperty<ItemStack> DISGUISE = new ModelProperty<>();
    public static final ModelProperty<String> DISGUISE_STATE = new ModelProperty<>();

    private final Predicate<RenderType> renderTypeLookup;

    public DisguiseBakedModel(IBakedModel originalModel) {
        this(originalModel, RenderType.getSolid());
    }

    public DisguiseBakedModel(IBakedModel originalModel, RenderType defaultRenderType) {
        this(originalModel, rt -> rt == defaultRenderType);
    }

    public DisguiseBakedModel(IBakedModel originalModel, Predicate<RenderType> renderTypeLookup) {
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

                BlockState disguiseState = disguiseItem.getBlock().getDefaultState();
                disguiseState = DisguiseStates.processDisguiseStates(disguiseState, extraData.getData(DISGUISE_STATE), state.get(BlockStateProperties.FACING));

                if (RenderTypeLookup.canRenderInLayer(disguiseState, MinecraftForgeClient.getRenderLayer())) {
                    IBakedModel model = Minecraft.getInstance().getBlockRendererDispatcher().getBlockModelShapes().getModel(disguiseState);

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
        if (renderTypeLookup.test(MinecraftForgeClient.getRenderLayer())) {
            return super.getQuads(state, side, rand, extraData);
        } else {
            return Collections.emptyList();
        }
    }

    @Nonnull
    @Override
    public IModelData getModelData(@Nonnull IBlockDisplayReader world, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nonnull IModelData tileData) {
        return super.getModelData(world, pos, state, tileData);
    }
}
