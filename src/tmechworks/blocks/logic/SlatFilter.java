package tmechworks.blocks.logic;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

//Lets XP orbs through, and items that are not itemBlocks.
public class SlatFilter extends SubFilter
{

	@Override
	public boolean canPass(Entity entity)
	{
		if(entity instanceof EntityXPOrb)
		{
			return true;
		}
		else if(entity instanceof EntityItem)
		{
			EntityItem check = (EntityItem)entity;
			return canPass(check.getEntityItem());
		}
		return false;
	}

	@Override
	public boolean canPass(ItemStack itemStack) {
		if(itemStack.getItem() instanceof ItemBlock)
		{
			return false;
		}
		return true;
	}
}
