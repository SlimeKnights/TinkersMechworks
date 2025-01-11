package slimeknights.tmechworks.library;

import com.google.common.collect.Lists;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraftforge.common.ForgeI18n;

import java.util.List;

public class TranslationUtil {
    public static List<Component> getTooltips(String text) {
        List<Component> list = Lists.newLinkedList();
        if (!ForgeI18n.getPattern(text).equals(text)) {
            String translate = ForgeI18n.getPattern(text);
            if (!ForgeI18n.getPattern(translate).equals(translate)) {
                String[] strings = Component.translatable(translate).getString().split("\n");

                for (String string : strings) {
                    list.add(Component.literal(string).withStyle(ChatFormatting.GRAY));
                }
            }
            else {
                String[] strings = Component.translatable(text).getString().split("\n");

                for (String string : strings) {
                    list.add(Component.literal(string).withStyle(ChatFormatting.GRAY));
                }
            }
        }

        return list;
    }
}
