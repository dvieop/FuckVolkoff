package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.killtracker.config;

import dev.lyons.configapi.ConfigAPI;
import dev.lyons.configapi.ConfigClass;
import dev.lyons.configapi.EnumFormattingType;
import dev.lyons.configapi.annotations.Config;
import lombok.Getter;
import lombok.Setter;
import xyz.velocity.VelocityFeatures;

import java.io.File;

@Config("killTracker")
public class KilltrackerConfig implements ConfigClass {

    public static KilltrackerConfig getInstance() {
        return ConfigAPI.getInstance().getConfigObject(KilltrackerConfig.class);
    }

    @Getter
    @Setter
    @Config
    public boolean enabled = false;

    @Getter
    @Config
    public String deathMessage = "<killer> has slain <victim> with <item>";

    @Override
    public EnumFormattingType getFormatType() {
        return EnumFormattingType.YAML;
    }

    @Override
    public File getFile() {
        return new File(
                VelocityFeatures.getFileUtils().getConfigDir("KillTracker"),
                "killTracker.yml"
        );
    }

    @Override
    public boolean isStatsConfig() {
        return false;
    }
}
