package slimeknights.tmechworks.integration;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.ISubtypeRegistration;
import net.minecraft.client.renderer.Rectangle2d;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
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
        registration.registerSubtypeInterpreter(MechworksContent.Blocks.firestarter.asItem(), stack -> {
            CompoundNBT nbt = stack.getTag();

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
            public List<Rectangle2d> getGuiExtraAreas(DrawbridgeScreen gui) {
                List<Rectangle2d> rects = new ArrayList<>();

                int guiLeft = gui.getGuiLeft();
                int guiTop = gui.getGuiTop();
                int xSize = gui.getXSize();
                int ySize = gui.getYSize();

                rects.add(new Rectangle2d(guiLeft - 44, guiTop + ySize - 65, 47, 60)); // Upgrades cutout
                rects.add(new Rectangle2d(guiLeft + xSize - 3, guiTop + ySize - 37, 29, 32)); // Disguise cutout

                if(gui.isAdvanced){
                    rects.add(new Rectangle2d(guiLeft - 18, guiTop - 80, 213, 148)); // Advanced UI
                    rects.add(new Rectangle2d(guiLeft + 191, guiTop + 4, 63, 60)); // Advanced arrows
                }

                return rects;
            }
        });
    }
}
