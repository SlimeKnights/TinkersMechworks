package slimeknights.tmechworks.common.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraftforge.common.ToolType;

public class MetalBlock extends Block
{
    public MetalBlock()
    {
        super(Properties.create(Material.IRON).hardnessAndResistance(5F, 10F).harvestTool(ToolType.PICKAXE).harvestLevel(-1).sound(SoundType.METAL));
    }
}
