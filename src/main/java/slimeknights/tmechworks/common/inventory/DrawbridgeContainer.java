package slimeknights.tmechworks.common.inventory;

import com.google.common.collect.ImmutableList;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import slimeknights.mantle.inventory.BaseContainer;
import slimeknights.tmechworks.common.MechworksContent;
import slimeknights.tmechworks.common.blocks.tileentity.DrawbridgeTileEntity;
import slimeknights.tmechworks.common.inventory.slots.ValidatingSlot;

import java.util.ArrayList;

public class DrawbridgeContainer extends BaseContainer<DrawbridgeTileEntity> {
    public static final int ADVANCED_COLUMNS = 11;

    public final PlayerInventory playerInventory;
    public final int rows;

    public final ImmutableList<Slot> mainSlots;

    public DrawbridgeContainer(int id, PlayerInventory playerInventory, DrawbridgeTileEntity te) {
        super(MechworksContent.Containers.drawbridge, id, te);

        this.playerInventory = playerInventory;
        te.openInventory(playerInventory.player);

        int slotCount = te.slots.getSizeInventory();
        rows = MathHelper.ceil((float)te.slots.getSizeInventory() / ADVANCED_COLUMNS);
        mainSlots = addDrawbridgeSlots();

        for(int x = 0; x < 2; x++){
            for(int y = 0; y < 2; y++){
                addSlot(new ValidatingSlot(tile.upgrades, x * 2 + y, -36 + x * 18, 119 + y * 18));
            }
        }

        addPlayerInventory(playerInventory, 8, 84);
    }

    protected ImmutableList<Slot> addDrawbridgeSlots(){
        if(!tile.stats.isAdvanced) {
            return ImmutableList.of(addSlot(new ValidatingSlot(tile.slots, 0, 80, 36)));
        } else {
            ArrayList<Slot> slots = new ArrayList<>();

            int slotCount = tile.slots.getSizeInventory();

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

    @OnlyIn(Dist.CLIENT)
    public static DrawbridgeContainer factory(int id, PlayerInventory playerInventory, PacketBuffer extraData) {
        BlockPos pos = extraData.readBlockPos();

        TileEntity te = playerInventory.player.world.getTileEntity(pos);
        DrawbridgeTileEntity drawbridge = null;

        if(te instanceof DrawbridgeTileEntity)
            drawbridge = (DrawbridgeTileEntity) te;

        return new DrawbridgeContainer(id, playerInventory, drawbridge);
    }
}
