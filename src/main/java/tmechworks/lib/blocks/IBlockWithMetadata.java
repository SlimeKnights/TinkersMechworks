package tmechworks.lib.blocks;

public interface IBlockWithMetadata
{
    //Gets an unlocalized name for the block by damage value.
    String getUnlocalizedNameByMetadata (int damageValue);

    //Number of valid metadata states associated with item names.
    int getItemCount ();
}
