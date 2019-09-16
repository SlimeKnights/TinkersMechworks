package slimeknights.tmechworks.common;

public class MechworksConfig {
    private static MechworksConfig instance = new MechworksConfig();

    public int drawbridgeExtendLength = 16;
    public int drawbridgeExtendUpgradeValue = 16;
    public float drawbridgeSpeed = 0.5F;
    public float drawbridgeSpeedUpgradeValue = 0.1F;

    public static MechworksConfig getInstance() {
        return instance;
    }
}
