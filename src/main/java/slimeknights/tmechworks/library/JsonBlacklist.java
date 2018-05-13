package slimeknights.tmechworks.library;

import net.minecraft.block.Block;
import net.minecraft.util.EnumFacing;

import java.util.List;

public class JsonBlacklist {

    public String blockName;
    public List<EnumFacing> facings;

    public JsonBlacklist(Block block, List<EnumFacing> facingList){
        this.blockName = block.getRegistryName().toString();
        this.facings = facingList;
    }

    public String getBlock() {
        return blockName;
    }

    public List<EnumFacing> getFacings() {
        return facings;
    }

    public void setBlock(String block) {
        this.blockName = block;
    }

    public void setFacings(List<EnumFacing> facings) {
        this.facings = facings;
    }

    public boolean exists(){
        Block block = Block.getBlockFromName(blockName);
        return block != null;
    }

    public Block getTrueBlock(){
        return Block.getBlockFromName(blockName);
    }
}