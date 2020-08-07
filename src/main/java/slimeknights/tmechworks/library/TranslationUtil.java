package slimeknights.tmechworks.library;

import com.google.common.collect.Lists;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.ForgeI18n;

import java.util.List;

public class TranslationUtil {
    public static List<ITextComponent> getTooltips(String text) {
        List<ITextComponent> list = Lists.newLinkedList();
        if (!ForgeI18n.getPattern(text).equals(text)) {
            String translate = ForgeI18n.getPattern(text);
            if (!ForgeI18n.getPattern(translate).equals(translate)) {
                String[] strings = new TranslationTextComponent(translate).getString().split("\n");

                for (String string : strings) {
                    list.add(new StringTextComponent(string).mergeStyle(TextFormatting.GRAY));
                }
            }
            else {
                String[] strings = new TranslationTextComponent(text).getString().split("\n");

                for (String string : strings) {
                    list.add(new StringTextComponent(string).mergeStyle(TextFormatting.GRAY));
                }
            }
        }

        return list;
    }
}
