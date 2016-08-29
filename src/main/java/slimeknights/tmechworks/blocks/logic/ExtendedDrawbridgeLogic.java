package slimeknights.tmechworks.blocks.logic;

public class ExtendedDrawbridgeLogic extends DrawbridgeLogic {
    @Override
    public void setupStatistics(DrawbridgeStats ds) {
        ds.extendLength = 64;
    }

    @Override
    public String getVariantName() {
        return "extended";
    }
}
