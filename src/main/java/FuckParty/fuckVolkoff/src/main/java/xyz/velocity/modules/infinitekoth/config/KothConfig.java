package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.infinitekoth.config;

import dev.lyons.configapi.ConfigAPI;
import dev.lyons.configapi.ConfigClass;
import dev.lyons.configapi.EnumFormattingType;
import dev.lyons.configapi.annotations.Config;
import lombok.Setter;
import xyz.velocity.VelocityFeatures;
import lombok.Getter;
import xyz.velocity.modules.infinitekoth.config.saves.HologramSave;
import xyz.velocity.modules.infinitekoth.config.saves.RewardSave;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

@Config("InfiniteKoth")
public class KothConfig implements ConfigClass {

    public static KothConfig getInstance() {
        return ConfigAPI.getInstance().getConfigObject(KothConfig.class);
    }

    @Getter
    @Setter
    @Config
    public boolean enabled = false;

    @Getter
    @Config
    String playerEnter = "&9&l<player> has entered the infinite koth";

    @Getter
    @Config
    String playerLeft = "&9&l<player> has left the infinite koth";

    @Getter
    @Config
    String tierUpgrade = "&9&l<player> is now on <tier>";

    @Getter
    @Config
    int tierUpgradeAfter = 900;

    @Getter
    @Config
    String kothReminder = "<player> has been capturing koth for <total_time>! Tier: <tier>";

    @Getter
    @Config
    int remindInterval = 600;

    @Getter
    @Config
    String gainReward = "You've received <reward>";

    @Getter
    @Config
    String location1 = "80:200:90:world";

    @Getter
    @Config
    String location2 = "83:200:93:world";

    @Getter
    @Config
    int rewardInterval = 300;

    @Getter
    @Config
    HologramSave hologram = new HologramSave("Infinite Koth", new ArrayList<String>(Collections.singleton("example")), "80:200:90:world");

    @Getter
    @Config
    RewardSave reward = new RewardSave(1);

    @Override
    public EnumFormattingType getFormatType() {
        return EnumFormattingType.YAML;
    }

    @Override
    public File getFile() {
        return new File(
                VelocityFeatures.getFileUtils().getConfigDir("Koth"),
                "InfiniteKoth.yml"
        );
    }

    @Override
    public boolean isStatsConfig() {
        return false;
    }

    public void saveConfig() {
        ConfigAPI.getInstance().saveConfig(this);
    }

}
