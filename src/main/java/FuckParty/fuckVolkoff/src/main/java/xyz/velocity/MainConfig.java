package FuckParty.fuckVolkoff.src.main.java.xyz.velocity;

import dev.lyons.configapi.ConfigAPI;
import dev.lyons.configapi.ConfigClass;
import dev.lyons.configapi.EnumFormattingType;
import dev.lyons.configapi.annotations.Config;
import lombok.Getter;

import java.io.File;

@Config("mainConfig")
public class MainConfig implements ConfigClass {

    public static MainConfig getInstance() {
        return ConfigAPI.getInstance().getConfigObject(MainConfig.class);
    }

    @Getter
    @Config
    public String commandPrefix = "";

    @Override
    public EnumFormattingType getFormatType() {
        return EnumFormattingType.YAML;
    }

    @Override
    public File getFile() {
        return new File(
                VelocityFeatures.getFileUtils().getConfigDir("Main"),
                "config.yml"
        );
    }

    @Override
    public boolean isStatsConfig() {
        return false;
    }

}
