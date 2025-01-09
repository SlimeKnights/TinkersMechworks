package slimeknights.tmechworks.common.inventory;

import com.google.common.collect.ImmutableList;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import slimeknights.mantle.inventory.BaseContainerMenu;
import slimeknights.tmechworks.common.MechworksContent;
import slimeknights.tmechworks.common.blocks.tileentity.DrawbridgeTileEntity;
import slimeknights.tmechworks.common.inventory.slots.ValidatingSlot;

import java.util.ArrayList;

public class DrawbridgeContainer extends BaseContainerMenu<DrawbridgeTileEntity> {
    public static final int ADVANCED_COLUMNS = 11;

    public final Inventory playerInventory;
    public final int rows;

    public final ImmutableList<Slot> mainSlots;

    public DrawbridgeContainer(int id, Inventory playerInventory, DrawbridgeTileEntity te) {
        super(MechworksContent.Containers.drawbridge.get(), id, playerInventory, te);

        this.playerInventory = playerInventory;
        te.startOpen(playerInventory.player);

        rows = Mth.ceil((float)te.slots.getContainerSize() / ADVANCED_COLUMNS);
        mainSlots = addDrawbridgeSlots();

        for(int x = 0; x < 2; x++){
            for(int y = 0; y < 2; y++){
                addSlot(new ValidatingSlot(tile.upgrades, x * 2 + y, -36 + x * 18, 119 + y * 18));
            }
        }

        addSlot(new ValidatingSlot(tile.getDisguiseInventory(), 0, 178, 137));

        addInventorySlots();
    }

    protected ImmutableList<Slot> addDrawbridgeSlots(){
        if(!tile.stats.isAdvanced) {
            return ImmutableList.of(addSlot(new ValidatingSlot(tile.slots, 0, 80, 36)));
        } else {
            ArrayList<Slot> slots = new ArrayList<>();

            int slotCount = tile.slots.getContainerSize();

            final int left = -18 + 7;
            final int width = 198;
            final int height = 126;

            final int top = -80 + 12 + height / 2 - rows * 18 / 2;

            for(int y = 0; y < rows; y++) {
                int cols = Math.min(slotCount - ADVANCED_COLUMNS * y, ADVANCED_COLUMNS);
                int yCoord = top + y * 18;
                int xStart = left + width / 2 - cols * 18 / 2;

                for (int x = 0; x < cols; x++) {
                    int xCoord = xStart + x * 18;

                    slots.add(addSlot(new ValidatingSlot(tile.slots, y * ADVANCED_COLUMNS + x, xCoord + 1, yCoord + 1)));
                }
            }

            return ImmutableList.copyOf(slots);
        }
    }

    public static DrawbridgeContainer factory(int id, Inventory playerInventory, FriendlyByteBuf extraData) {
        BlockPos pos = extraData.readBlockPos();

        BlockEntity te = playerInventory.player.level.getBlockEntity(pos);
        DrawbridgeTileEntity drawbridge = null;

        if(te instanceof DrawbridgeTileEntity)
            drawbridge = (DrawbridgeTileEntity) te;

        return new DrawbridgeContainer(id, playerInventory, drawbridge);
    }
}
