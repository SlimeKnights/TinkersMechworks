package slimeknights.tmechworks.common.inventory;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import slimeknights.mantle.inventory.BaseContainer;
import slimeknights.tmechworks.common.MechworksContent;
import slimeknights.tmechworks.common.blocks.tileentity.RedstoneMachineTileEntity;
import slimeknights.tmechworks.common.inventory.slots.ValidatingSlot;

public class DisguiseContainer extends BaseContainer<RedstoneMachineTileEntity> {
    public DisguiseContainer(int windowId, PlayerInventory playerInventory, RedstoneMachineTileEntity tile) {
        super(MechworksContent.Containers.disguise, windowId, tile);

        tile.openInventory(playerInventory.player);

        addSlot(new ValidatingSlot(tile.getDisguiseInventory(), 0, 80, 34));
        addPlayerInventory(playerInventory, 8, 84);
    }

    public static DisguiseContainer factory(int id, PlayerInventory playerInventory, PacketBuffer extraData) {
        BlockPos pos = extraData.readBlockPos();

        TileEntity te = playerInventory.player.world.getTileEntity(pos);
        RedstoneMachineTileEntity machine = null;

        if(te instanceof RedstoneMachineTileEntity)
            machine = (RedstoneMachineTileEntity) te;

        return new DisguiseContainer(id, playerInventory, machine);
    }
}
