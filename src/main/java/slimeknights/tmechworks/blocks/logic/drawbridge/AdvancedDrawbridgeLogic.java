package slimeknights.tmechworks.blocks.logic.drawbridge;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import slimeknights.tmechworks.client.gui.GuiDrawbridgeAdvanced;
import slimeknights.tmechworks.inventory.ContainerDrawbridgeAdvanced;

public class AdvancedDrawbridgeLogic extends DrawbridgeLogic {
    public AdvancedDrawbridgeLogic(){
        super(16);
    }

    @Override
    public int getNextIndex() {
        return getExtendState();
    }
    @Override
    public int getLastIndex() {
        return getExtendState();
    }

    @Override
    public String getVariantName() {
        return "advanced";
    }

    @Override
    public int getInventoryStackLimit() {
        return 1;
    }

    @Override
    public Container createContainer(InventoryPlayer inventoryplayer, World world, BlockPos pos) {
        return new ContainerDrawbridgeAdvanced(this, inventoryplayer);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public GuiContainer createGui(InventoryPlayer inventoryplayer, World world, BlockPos pos) {
        return new GuiDrawbridgeAdvanced(new ContainerDrawbridgeAdvanced(this, inventoryplayer));
    }
}
