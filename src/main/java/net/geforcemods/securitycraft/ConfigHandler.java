package net.geforcemods.securitycraft;

import me.sargunvohra.mcmods.autoconfig1u.ConfigData;
import me.sargunvohra.mcmods.autoconfig1u.annotation.Config;

@Config(name = SecurityCraft.MODID)
public class ConfigHandler implements ConfigData {
    public static ConfigHandler CONFIG;

    public boolean allowCodebreakerItem = true;
    public boolean allowAdminTool = false;
    public boolean shouldSpawnFire = true;
    public boolean ableToBreakMines = true;
    public boolean ableToCraftKeycard1 = true;
    public boolean ableToCraftKeycard2 = true;
    public boolean ableToCraftKeycard3 = true;
    public boolean ableToCraftKeycard4 = true;
    public boolean ableToCraftKeycard5 = true;
    public boolean ableToCraftLUKeycard = true;
    public boolean smallerMineExplosion = false;
    public boolean mineExplodesWhenInCreative = true;
    public double portableRadarSearchRadius = 25.0D;
    public int usernameLoggerSearchRadius = 3;
    public int laserBlockRange = 5;
    public int alarmTickDelay = 2;
    public int portableRadarDelay = 4;
    public int claymoreRange = 5;
    public int imsRange = 12;
    public int inventoryScannerRange = 2;
    public int maxAlarmRange = 100;
    public double motionActivatedLightSearchRadius = 5.0D;
    public boolean allowBlockClaim = false;
    public boolean sayThanksMessage = true;
    public double alarmSoundVolume = 0.3D;
    public double cameraSpeed = 2.0D;
    public boolean respectInvisibility = false;
    public boolean reinforcedBlockTint = true;
    public boolean ableToCraftMines = true;
    public boolean retinalScannerFace = true;
}
