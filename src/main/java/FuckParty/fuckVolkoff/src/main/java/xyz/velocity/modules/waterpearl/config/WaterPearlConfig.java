package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.waterpearl.config;

import dev.lyons.configapi.ConfigAPI;
import dev.lyons.configapi.ConfigClass;
import dev.lyons.configapi.EnumFormattingType;
import dev.lyons.configapi.annotations.Config;
import lombok.Getter;
import lombok.Setter;
import xyz.velocity.VelocityFeatures;

import java.io.File;

@Config
public class WaterPearlConfig implements ConfigClass {

    public static WaterPearlConfig getInstance() {
        return ConfigAPI.getInstance().getConfigObject(WaterPearlConfig.class);
    }

    @Getter
    @Setter
    @Config
    public boolean enabled = false;

    @Override
    public EnumFormattingType getFormatType() {
        return EnumFormattingType.YAML;
    }

    @Override
    public File getFile() {
        return new File(
                VelocityFeatures.getFileUtils().getConfigDir("WaterPearls"),
                "water-pearls.yml"
        );
    }

    @Override
    public boolean isStatsConfig() {
        return false;
    }

}
