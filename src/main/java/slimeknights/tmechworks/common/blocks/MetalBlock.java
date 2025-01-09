package slimeknights.tmechworks.common.blocks;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;

public class MetalBlock extends Block
{
    public MetalBlock()
    {
        super(Properties.of(Material.METAL).strength(5F, 10F).sound(SoundType.METAL));
    }
}
