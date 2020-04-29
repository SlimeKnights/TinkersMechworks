package slimeknights.tmechworks.integration.waila;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public interface IInformationProvider {
    @OnlyIn(Dist.CLIENT)
    default void getInformation(@Nonnull List<ITextComponent> info, @Nonnull InformationType type, PlayerEntity player) {

    }

    @OnlyIn(Dist.CLIENT)
    default void getInformation(@Nonnull List<ITextComponent> info, @Nonnull InformationType type, CompoundNBT serverData, PlayerEntity player) {
        getInformation(info, type, player);
    }

    default void syncInformation(CompoundNBT nbt, ServerPlayerEntity player) {

    }

    default void requireSneak(List<ITextComponent> tooltip, PlayerEntity player, Runnable action) {
        if(!player.isCrouching()) {
            tooltip.add(new TranslationTextComponent("tooltip.waila.sneak_for_details").applyTextStyle(TextFormatting.ITALIC));
        } else {
            action.run();
        }
    }

    enum InformationType {
        HEAD, BODY, TAIL
    }
}
