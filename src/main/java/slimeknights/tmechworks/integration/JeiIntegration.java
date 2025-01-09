package slimeknights.tmechworks.integration;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.ISubtypeRegistration;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import slimeknights.tmechworks.client.gui.DrawbridgeScreen;
import slimeknights.tmechworks.common.MechworksContent;
import slimeknights.tmechworks.library.Util;

import java.util.ArrayList;
import java.util.List;

@JeiPlugin
public class JeiIntegration implements IModPlugin {
    @Override
    public ResourceLocation getPluginUid() {
        return Util.getResource("jei");
    }

    @Override
    public void registerItemSubtypes(ISubtypeRegistration registration) {
        registration.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, MechworksContent.Blocks.firestarter.asItem(), (stack, ctx) -> {
            CompoundTag nbt = stack.getTag();

            boolean shouldExtinguish = true;

            if(nbt != null)
                shouldExtinguish = nbt.getBoolean("extinguish");

            return shouldExtinguish ? "extinguish" : "keepLit";
        });
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addGuiContainerHandler(DrawbridgeScreen.class, new IGuiContainerHandler<DrawbridgeScreen>() {
            @Override
            public List<Rect2i> getGuiExtraAreas(DrawbridgeScreen gui) {
                List<Rect2i> rects = new ArrayList<>();

                int guiLeft = gui.getGuiLeft();
                int guiTop = gui.getGuiTop();
                int xSize = gui.getXSize();
                int ySize = gui.getYSize();

                rects.add(new Rect2i(guiLeft - 44, guiTop + ySize - 65, 47, 60)); // Upgrades cutout

                int disguiseStateWidth = 0;
                if(gui.disguiseWidget.getColumnCount() > 0){
                    disguiseStateWidth += 5 + gui.disguiseWidget.getColumnCount() * 8;
                }
                rects.add(new Rect2i(guiLeft + xSize - 3, guiTop + ySize - 37, 29 + disguiseStateWidth, 32)); // Disguise cutout

                if(gui.isAdvanced){
                    rects.add(new Rect2i(guiLeft - 18, guiTop - 80, 213, 148)); // Advanced UI
                    rects.add(new Rect2i(guiLeft + 191, guiTop + 4, 63, 60)); // Advanced arrows
                }

                return rects;
            }
        });
    }
}
