package slimeknights.tmechworks.common;

public class MechworksConfig {
    private static MechworksConfig instance = new MechworksConfig();

    // Total extend length designed to be no higher than 64 blocks for advanced drawbridges, therefore going higher than 64 may produce weird results
    public int drawbridgeExtendLength = 16;
    public int drawbridgeExtendUpgradeValue = 16;
    public float drawbridgeSpeed = 0.5F;
    public float drawbridgeSpeedUpgradeValue = 0.1F;

    public static MechworksConfig getInstance() {
        return instance;
    }
}
