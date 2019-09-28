package slimeknights.tmechworks.common;

import net.minecraft.block.Block;
import net.minecraft.block.OreBlock;
import net.minecraft.block.material.Material;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTier;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import slimeknights.mantle.client.CreativeTab;
import slimeknights.mantle.common.IRegisterUtil;
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
import slimeknights.tmechworks.common.items.MechworksBlockItem;
import slimeknights.tmechworks.common.items.MechworksBookItem;
import slimeknights.tmechworks.common.items.MechworksItem;
import slimeknights.tmechworks.common.worldgen.MechworksWorld;

public class MechworksContent implements IRegisterUtil {
    private Logger log = LogManager.getLogger(TMechworks.modId + ".content");

    // Creative tabs
    public static CreativeTab tabMechworks = new CreativeTab("TinkersMechworks", new ItemStack(net.minecraft.item.Items.LIME_BANNER));

    @ObjectHolder(TMechworks.modId)
    public static class Blocks {
        public static final OreBlock aluminum_ore = null;
        public static final OreBlock copper_ore = null;
        public static final MetalBlock aluminum_block = null;
        public static final MetalBlock copper_block = null;
        public static final FirestarterBlock firestarter = null;
        public static final DrawbridgeBlock drawbridge = null;
    }

    @ObjectHolder(TMechworks.modId)
    public static class Items {
        public static final MechworksBookItem book = null;

        // Upgrades
        public static final MechworksItem upgrade_blank = null;
        public static final MachineUpgradeItem upgrade_drawbridge_advanced = null;
        public static final MachineUpgradeItem upgrade_drawbridge_distance = null;
        public static final MachineUpgradeItem upgrade_speed = null;
    }

    @ObjectHolder(TMechworks.modId)
    public static class TileEntities {
        public static final TileEntityType<?> firestarter = null;
        public static final TileEntityType<?> drawbridge = null;
    }

    @ObjectHolder(TMechworks.modId)
    public static class Containers {
        public static final ContainerType<DisguiseContainer> disguise = null;
        public static final ContainerType<DrawbridgeContainer> drawbridge = null;
    }

    @Override
    public String getModId() {
        return TMechworks.modId;
    }

    @SubscribeEvent
    public void registerBlocks(final RegistryEvent.Register<Block> event) {
        IForgeRegistry<Block> registry = event.getRegistry();

        // Ores
        register(registry, new OreBlock(Block.Properties.create(Material.ROCK).hardnessAndResistance(3F).harvestTool(ToolType.PICKAXE).harvestLevel(ItemTier.IRON.getHarvestLevel())), "aluminum_ore");
        register(registry, new OreBlock(Block.Properties.create(Material.ROCK).hardnessAndResistance(3F).harvestTool(ToolType.PICKAXE).harvestLevel(ItemTier.IRON.getHarvestLevel())), "copper_ore");

        // Metals
        register(registry, new MetalBlock(), "aluminum_block");
        register(registry, new MetalBlock(), "copper_block");

        // Machines
        register(registry, new FirestarterBlock(), "firestarter");
        register(registry, new DrawbridgeBlock(), "drawbridge");
    }

    @SubscribeEvent
    public void registerItems(final RegistryEvent.Register<Item> event) {
        IForgeRegistry<Item> registry = event.getRegistry();

        register(registry, new MechworksBookItem(), "book");

        // Ores
        registerBlockItem(registry, Blocks.aluminum_ore);
        registerBlockItem(registry, Blocks.copper_ore);

        // Metals
        registerBlockItem(registry, Blocks.copper_block);
        registerBlockItem(registry, Blocks.aluminum_block);

        register(registry, new MechworksItem(), "copper_ingot");
        register(registry, new MechworksItem(), "aluminum_ingot");
        register(registry, new MechworksItem(), "copper_nugget");
        register(registry, new MechworksItem(), "aluminum_nugget");

        // Machines
        registerBlockItem(registry, Blocks.firestarter);
        registerBlockItem(registry, Blocks.drawbridge);

        // Machine Upgrades
        register(registry, new MechworksItem(), "upgrade_blank");
        register(registry, new MachineUpgradeItem(stats -> stats.isAdvanced = true), "upgrade_drawbridge_advanced");
        register(registry, new MachineUpgradeItem(stats -> stats.extendLength += MechworksConfig.DRAWBRIDGE.extendUpgradeValue.get()).setTooltipFormatSupplier(() -> new Object[]{MechworksConfig.DRAWBRIDGE.extendUpgradeValue.get()}), "upgrade_drawbridge_distance");
        register(registry, new MachineUpgradeItem(stats -> stats.extendDelay -= MechworksConfig.DRAWBRIDGE.speedUpgradeValue.get()).setTooltipFormatSupplier(() -> new Object[]{MechworksConfig.DRAWBRIDGE.speedUpgradeValue.get()}), "upgrade_speed");
    }

    @SubscribeEvent
    public void registerTileEntities(final RegistryEvent.Register<TileEntityType<?>> event) {
        IForgeRegistry<TileEntityType<?>> registry = event.getRegistry();

        registerTE(registry, FirestarterTileEntity::new, "firestarter", Blocks.firestarter);
        registerTE(registry, DrawbridgeTileEntity::new, "drawbridge", Blocks.drawbridge);
    }

    @SubscribeEvent
    public void registerContainers(final RegistryEvent.Register<ContainerType<?>> event) {
        IForgeRegistry<ContainerType<?>> registry = event.getRegistry();

        register(registry, IForgeContainerType.create(DisguiseContainer::factory), "disguise");
        register(registry, IForgeContainerType.create(DrawbridgeContainer::factory), "drawbridge");
    }

    @OnlyIn(Dist.CLIENT)
    public void registerScreenFactories() {
        ScreenManager.registerFactory(Containers.disguise, DisguiseScreen::create);
        ScreenManager.registerFactory(Containers.drawbridge, DrawbridgeScreen::create);
    }

//    public void registerEntities(final RegistryEvent.Register<EntityType<?>> event){}

    public void preInit(FMLCommonSetupEvent event) {
    }

    public void init(InterModEnqueueEvent event) {
    }

    public void postInit(InterModProcessEvent event) {
        tabMechworks.setDisplayIcon(new ItemStack(Items.book));
        MechworksWorld.registerWorldGeneration();
    }

    @Override
    public BlockItem registerBlockItem(IForgeRegistry<Item> registry, Block block) {
        BlockItem itemBlock = new MechworksBlockItem(block, new Item.Properties().group(tabMechworks));
        return this.register(registry, itemBlock, block.getRegistryName());
    }
}
