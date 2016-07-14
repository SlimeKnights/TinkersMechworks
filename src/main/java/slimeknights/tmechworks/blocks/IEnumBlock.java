package slimeknights.tmechworks.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.util.IStringSerializable;

public interface IEnumBlock<E extends Enum<E> & IStringSerializable>
{
    PropertyEnum<E> getProperty ();

    Block getSelf ();
}
