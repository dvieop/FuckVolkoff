package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.stronghold.config;

import dev.lyons.configapi.ConfigAPI;
import dev.lyons.configapi.ConfigClass;
import dev.lyons.configapi.EnumFormattingType;
import dev.lyons.configapi.annotations.Config;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lombok.Getter;
import xyz.velocity.VelocityFeatures;
import xyz.velocity.modules.stronghold.config.saves.DataSave;
import xyz.velocity.modules.stronghold.Stronghold;

import java.io.File;

@Config("strongholdData")
public class DataConfig implements ConfigClass {

    public static DataConfig getInstance() {
        return ConfigAPI.getInstance().getConfigObject(DataConfig.class);
    }

    @Getter
    @Config
    public Object2ObjectOpenHashMap<String, DataSave> strongholds = new Object2ObjectOpenHashMap<>();

    @Override
    public EnumFormattingType getFormatType() {
        return EnumFormattingType.YAML;
    }

    @Override
    public File getFile() {
        return new File(
                VelocityFeatures.getFileUtils().getConfigDir("Stronghold"),
                "data.yml"
        );
    }

    @Override
    public boolean isStatsConfig() {
        return true;
    }

    public void addToList() {
        Stronghold.getInstance().capturePoints.forEach(capturePoint -> {
            if (!strongholds.containsKey(capturePoint.getStronghold().getName())) {
                DataSave dataSave = new DataSave("", 0, true, 0);
                strongholds.put(capturePoint.getStronghold().getName(), dataSave);

                ConfigAPI.getInstance().saveConfig(this);
            }
        });
    }

}
