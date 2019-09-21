package slimeknights.tmechworks.integration;

import com.google.common.collect.ImmutableList;
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
                // Upgrades UI cutout
                return ImmutableList.of(new Rectangle2d(gui.getGuiLeft() - 44, gui.getGuiTop() + gui.getYSize() - 65, 47, 60));
            }
        });
    }
}
