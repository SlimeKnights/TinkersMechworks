package slimeknights.tmechworks.blocks;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import slimeknights.tmechworks.blocks.logic.FirestarterLogic;
import slimeknights.tmechworks.library.Util;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class Firestarter extends RedstoneMachine<RedstoneMachine.DefaultTypes>
{
    public static final PropertyBool SHOULD_EXTINGUISH = PropertyBool.create("extinguish");

    public Firestarter ()
    {
        super(Material.IRON, DEF_TYPE, DefaultTypes.class);
    }

    @Override public IBlockState getActualState (@Nonnull IBlockState state, IBlockAccess worldIn, BlockPos pos)
    {
        FirestarterLogic firestarter = (FirestarterLogic) worldIn.getTileEntity(pos);

        boolean extinguishFire = true;

        if (firestarter != null)
        {
            extinguishFire = firestarter.getShouldExtinguish();
        }

        return super.getActualState(state, worldIn, pos).withProperty(SHOULD_EXTINGUISH, extinguishFire);
    }

    @Nonnull @Override protected BlockStateContainer createBlockState ()
    {
        return new BlockStateContainer(this, FACING, SHOULD_EXTINGUISH, DEF_TYPE);
    }

    @Nonnull @Override public TileEntity createNewTileEntity (@Nonnull World worldIn, int meta)
    {
        return new FirestarterLogic();
    }

    @Override
    public boolean onBlockActivated (World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing side, float hitX, float hitY,
            float hitZ)
    {
        FirestarterLogic logic = (FirestarterLogic) worldIn.getTileEntity(pos);

        if (logic != null)
        {
            logic.setShouldExtinguish(!logic.getShouldExtinguish());
            worldIn.playSound(playerIn, pos, SoundEvents.BLOCK_COMPARATOR_CLICK, SoundCategory.BLOCKS, 0.3F, 0.55F);
            logic.sync();
        }

        return true;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);

        boolean shouldExtinguish = false;

        if(stack.hasTagCompound()){
            shouldExtinguish = stack.getTagCompound().getCompoundTag("BlockEntityTag").getBoolean("ShouldExtinguish");
        }

        tooltip.add(I18n.format(Util.prefix("hud.behaviour")) + ": " + I18n.format(Util.prefix("hud.behaviour.firestarter." + (shouldExtinguish ? "extinguish" : "keep"))));
    }

    @Override public void setDefaultNBT(NBTTagCompound tags) {
        super.setDefaultNBT(tags);

        tags.setBoolean("ShouldExtinguish", false);
    }
}
