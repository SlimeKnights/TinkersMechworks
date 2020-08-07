package slimeknights.tmechworks.integration.waila;

import mcp.mobius.waila.api.IRegistrar;
import mcp.mobius.waila.api.IWailaPlugin;
import mcp.mobius.waila.api.TooltipPosition;
import mcp.mobius.waila.api.WailaPlugin;
import net.minecraft.util.ResourceLocation;
import slimeknights.tmechworks.library.Util;

@WailaPlugin
public class WailaIntegration implements IWailaPlugin {
    private static final ResourceLocation CONFIG_REDSTONE_MACHINE = Util.getResource("redstone_machine");

    @Override
    public void register(IRegistrar registrar) {
        registrar.addConfig(CONFIG_REDSTONE_MACHINE, true);

        registrar.registerComponentProvider(GenericTileDataProvider.INSTANCE, TooltipPosition.HEAD, IInformationProvider.class);
        registrar.registerComponentProvider(GenericTileDataProvider.INSTANCE, TooltipPosition.BODY, IInformationProvider.class);
        registrar.registerComponentProvider(GenericTileDataProvider.INSTANCE, TooltipPosition.TAIL, IInformationProvider.class);
        registrar.registerBlockDataProvider(GenericTileDataProvider.INSTANCE, IInformationProvider.class);
    }
}