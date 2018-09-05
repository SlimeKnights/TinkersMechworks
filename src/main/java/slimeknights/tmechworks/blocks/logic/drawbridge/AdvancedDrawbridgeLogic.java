package slimeknights.tmechworks.blocks.logic.drawbridge;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import slimeknights.tmechworks.client.gui.GuiDrawbridgeAdvanced;
import slimeknights.tmechworks.inventory.ContainerDrawbridgeAdvanced;

import java.util.Arrays;

public class AdvancedDrawbridgeLogic extends DrawbridgeLogic {
    private IBlockState[] placedState;

    public AdvancedDrawbridgeLogic(){
        super(16);
    }

    @Override
    public IBlockState getPlacedState(){
        if(placedState == null || getExtendState() >= placedState.length)
            return null;

        return placedState[getExtendState() - 1];
    }

    @Override
    public void setPlacedState(IBlockState state){
        if(placedState == null)
            placedState = new IBlockState[getStats().extendLength];
        else if(placedState.length != getStats().extendLength)
            placedState = Arrays.copyOf(placedState, getStats().extendLength);

        if(getExtendState() >= placedState.length)
            return;

        placedState[getExtendState()] = state;
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
