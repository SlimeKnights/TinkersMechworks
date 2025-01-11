package slimeknights.tmechworks.integration.waila;

import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import java.util.List;

public interface IInformationProvider {
    @OnlyIn(Dist.CLIENT)
    default void getInformation(@Nonnull List<Component> info, @Nonnull InformationType type, Player player) {

    }

    @OnlyIn(Dist.CLIENT)
    default void getInformation(@Nonnull List<Component> info, @Nonnull InformationType type, CompoundTag serverData, Player player) {
        getInformation(info, type, player);
    }

    default void syncInformation(CompoundTag nbt, ServerPlayer player) {

    }

    default void syncInformation(CompoundTag nbt, ServerPlayer player, boolean showDetails) {
        syncInformation(nbt, player);
        nbt.putBoolean("showDetails", showDetails);
    }

    default void showDetailsText(List<Component> tooltip, CompoundTag serverData) {
        if (!serverData.getBoolean("showDetails")) {
            tooltip.add(new TextComponent(""));
            tooltip.add(new TranslatableComponent("config.waila.display_mode_lite_desc").withStyle(ChatFormatting.ITALIC));
        }
    }

    enum InformationType {
        HEAD, BODY, TAIL
    }
}
