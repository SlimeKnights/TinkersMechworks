package slimeknights.tmechworks.inventory;

import net.minecraft.block.Block;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import slimeknights.tmechworks.blocks.logic.DrawbridgeLogicBase;
import slimeknights.tmechworks.library.JsonBlacklist;
import slimeknights.tmechworks.library.JsonConfig;

import java.util.List;

public class SlotBlacklist extends Slot{

    private DrawbridgeLogicBase tile;

    public SlotBlacklist(IInventory inventoryIn, int index, int xPosition, int yPosition) {
        super(inventoryIn, index, xPosition, yPosition);
        this.tile = (DrawbridgeLogicBase) inventoryIn;
    }

    public boolean isItemValid(ItemStack stack)
    {
        Block block;
        if(stack.getItem() instanceof ItemBlock){
            block = Block.getBlockFromItem(stack.getItem());
        } else {
            return false;
        }

        EnumFacing tileFacing = tile.getFacingDirection();
        List<JsonBlacklist> blacklisted = JsonConfig.blockBlacklist;
        for (JsonBlacklist jsonBlacklist : blacklisted) {
            if(block == jsonBlacklist.getTrueBlock()){
                if(jsonBlacklist.facings.isEmpty()){
                    return false;
                }
                if(jsonBlacklist.getFacings().contains(tileFacing)){
                    return false;
                }
            }
        }
        return true;
    }

}
