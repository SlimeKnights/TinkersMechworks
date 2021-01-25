package slimeknights.tmechworks.common;

import net.minecraft.block.Block;
import net.minecraft.block.OreBlock;
import net.minecraft.block.material.Material;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.*;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import slimeknights.mantle.registration.deferred.*;
import slimeknights.mantle.registration.object.ItemObject;
import slimeknights.mantle.util.SupplierItemGroup;
import slimeknights.tmechworks.TMechworks;
import slimeknights.tmechworks.client.gui.DisguiseScreen;
import slimeknights.tmechworks.client.gui.DrawbridgeScreen;
import slimeknights.tmechworks.common.blocks.DrawbridgeBlock;
import slimeknights.tmechworks.common.blocks.FirestarterBlock;
import slimeknights.tmechworks.common.blocks.MetalBlock;
import slimeknights.tmechworks.common.blocks.tileentity.DrawbridgeTileEntity;
import slimeknights.tmechworks.common.blocks.tileentity.FirestarterTileEntity;
import slimeknights.tmechworks.common.config.MechworksConfig;
import slimeknights.tmechworks.common.inventory.DisguiseContainer;
import slimeknights.tmechworks.common.inventory.DrawbridgeContainer;
import slimeknights.tmechworks.common.items.MachineUpgradeItem;
import slimeknights.tmechworks.common.items.MechworksBookItem;
import slimeknights.tmechworks.common.items.MechworksItem;
import slimeknights.tmechworks.common.worldgen.MechworksWorld;

import java.util.function.Function;

public class MechworksContent {
    private final Logger log = LogManager.getLogger(TMechworks.modId + ".content");

    private static final BlockDeferredRegister BLOCKS = new BlockDeferredRegister(TMechworks.modId);
    private static final ItemDeferredRegister ITEMS = new ItemDeferredRegister(TMechworks.modId);
    private static final TileEntityTypeDeferredRegister TILE_ENTITIES = new TileEntityTypeDeferredRegister(TMechworks.modId);
    private static final ContainerTypeDeferredRegister CONTAINERS = new ContainerTypeDeferredRegister(TMechworks.modId);

    // Creative tabs
    public static ItemGroup tabMechworks = new SupplierItemGroup(TMechworks.modId, "TinkersMechworks", () -> new ItemStack(Items.book)).setTabPath("");

    private static final Function<Block, ? extends BlockItem> DEFAULT_BLOCK_ITEM = (b) -> new BlockItem(b, new Item.Properties().group(tabMechworks));

    public static class Blocks {
        public static final ItemObject<OreBlock> aluminum_ore = BLOCKS.register("aluminum_ore", () -> new OreBlock(Block.Properties.create(Material.ROCK).hardnessAndResistance(3F).harvestTool(ToolType.PICKAXE).harvestLevel(ItemTier.IRON.getHarvestLevel())), DEFAULT_BLOCK_ITEM);
        public static final ItemObject<OreBlock> copper_ore = BLOCKS.register("copper_ore", () -> new OreBlock(Block.Properties.create(Material.ROCK).hardnessAndResistance(3F).harvestTool(ToolType.PICKAXE).harvestLevel(ItemTier.IRON.getHarvestLevel())), DEFAULT_BLOCK_ITEM);
        public static final ItemObject<MetalBlock> aluminum_block = BLOCKS.register("aluminum_block", MetalBlock::new, DEFAULT_BLOCK_ITEM);
        public static final ItemObject<MetalBlock> copper_block = BLOCKS.register("copper_block", MetalBlock::new, DEFAULT_BLOCK_ITEM);
        public static final ItemObject<FirestarterBlock> firestarter = BLOCKS.register("firestarter", FirestarterBlock::new, DEFAULT_BLOCK_ITEM);
        public static final ItemObject<DrawbridgeBlock> drawbridge = BLOCKS.register("drawbridge", DrawbridgeBlock::new, DEFAULT_BLOCK_ITEM);
    }

    public static class Items {
        public static final ItemObject<MechworksBookItem> book = ITEMS.register("book", MechworksBookItem::new);

        public static final ItemObject<MechworksItem> copper_ingot = ITEMS.register("copper_ingot", MechworksItem::new);
        public static final ItemObject<MechworksItem> aluminum_ingot = ITEMS.register("aluminum_ingot", MechworksItem::new);
        public static final ItemObject<MechworksItem> copper_nugget = ITEMS.register("copper_nugget", MechworksItem::new);
        public static final ItemObject<MechworksItem> aluminum_nugget = ITEMS.register("aluminum_nugget", MechworksItem::new);

        // Upgrades
        public static final ItemObject<Item> upgrade_blank = ITEMS.register("upgrade_blank", MechworksItem::new);
        public static final ItemObject<MachineUpgradeItem> upgrade_drawbridge_advanced = ITEMS.register("upgrade_drawbridge_advanced", () -> new MachineUpgradeItem(stats -> stats.isAdvanced = true));
        public static final ItemObject<MechworksItem> upgrade_drawbridge_distance = ITEMS.register("upgrade_drawbridge_distance", () -> new MachineUpgradeItem(stats -> stats.extendLength += MechworksConfig.COMMON_CONFIG.drawbridge.extendUpgradeValue.get()).setTooltipFormatSupplier(() -> new Object[]{MechworksConfig.COMMON_CONFIG.drawbridge.extendUpgradeValue.get()}));
        public static final ItemObject<MechworksItem> upgrade_speed = ITEMS.register("upgrade_speed", () -> new MachineUpgradeItem(stats -> stats.extendDelay -= MechworksConfig.COMMON_CONFIG.drawbridge.speedUpgradeValue.get()).setTooltipFormatSupplier(() -> new Object[]{MechworksConfig.COMMON_CONFIG.drawbridge.speedUpgradeValue.get()}));
    }

    public static class TileEntities {
        public static final RegistryObject<TileEntityType<FirestarterTileEntity>> firestarter = TILE_ENTITIES.register("firestarter", FirestarterTileEntity::new, Blocks.firestarter);
        public static final RegistryObject<TileEntityType<DrawbridgeTileEntity>> drawbridge = TILE_ENTITIES.register("drawbridge", DrawbridgeTileEntity::new, Blocks.drawbridge);
    }

    public static class Containers {
        public static final RegistryObject<ContainerType<DisguiseContainer>> disguise = CONTAINERS.register("disguise", DisguiseContainer::factory);
        public static final RegistryObject<ContainerType<DrawbridgeContainer>> drawbridge = CONTAINERS.register("drawbridge", DrawbridgeContainer::factory);
    }

    @OnlyIn(Dist.CLIENT)
    public void registerScreenFactories() {
        ScreenManager.registerFactory(Containers.disguise.get(), DisguiseScreen::create);
        ScreenManager.registerFactory(Containers.drawbridge.get(), DrawbridgeScreen::create);
    }

    public void initRegisters() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        createRegister(BLOCKS, bus, Blocks.aluminum_block);
        createRegister(ITEMS, bus, Items.aluminum_ingot);
        createRegister(TILE_ENTITIES, bus, TileEntities.drawbridge);
        createRegister(CONTAINERS, bus, Containers.disguise);
    }

    /**
     * Exists to force static initialization of the necessary subclass, takes an object from the subclass as parameter
     */
    private void createRegister(DeferredRegisterWrapper<?> wrapper, IEventBus bus, Object anything) {
        wrapper.register(bus);
    }

    public void preInit(FMLCommonSetupEvent event) {
    }

    public void init(InterModEnqueueEvent event) {
    }

    public void postInit(InterModProcessEvent event) {

    }
}
