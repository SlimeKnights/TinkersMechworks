package tmechworks.lib.blocks;

public enum PlacementType
{
    /*0: Metadata has to match            PlacementType.metaMatch
    1: Metadata has no meaning          PlacementType.metaIgnore
    2: Should not be placed             PlacementType.GTFO
    3: Has rotational metadata          PlacementType.rotationalMeta
    4: Rails                            PlacementType.rails
    5: Has rotational TileEntity data   PlacementType.rotationalTE
    6: Custom placement logic           PlacementType.custom*/

    metaMatch(0), metaIgnore(1), GTFO(2), rotationalMeta(3), rails(4), rotationalTE(5), custom(6);

    private final int typeID;

    private PlacementType(int typeID)
    {
        this.typeID = typeID;
    }

    public int getTypeID ()
    {
        return this.typeID;
    }

}
