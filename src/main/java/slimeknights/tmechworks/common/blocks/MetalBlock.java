package slimeknights.tmechworks.common.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraftforge.common.ToolType;

import net.minecraft.block.AbstractBlock.Properties;

public class MetalBlock extends Block
{
    public MetalBlock()
    {
        super(Properties.of(Material.METAL).strength(5F, 10F).harvestTool(ToolType.PICKAXE).harvestLevel(-1).sound(SoundType.METAL));
    }
}
