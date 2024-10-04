package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.garrison.config;

import dev.lyons.configapi.ConfigAPI;
import dev.lyons.configapi.ConfigClass;
import dev.lyons.configapi.EnumFormattingType;
import dev.lyons.configapi.annotations.Config;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import lombok.Getter;
import xyz.velocity.VelocityFeatures;
import xyz.velocity.modules.garrison.BoostCache;
import xyz.velocity.modules.garrison.EnumBoostMode;
import xyz.velocity.modules.garrison.Garrison;
import xyz.velocity.modules.garrison.config.saves.BoostDataSave;
import xyz.velocity.modules.garrison.config.saves.DataSave;
import xyz.velocity.modules.util.CapturePoint;

import java.io.File;

@Config("garrisonData")
public class DataConfig implements ConfigClass {

    public static DataConfig getInstance() {
        return ConfigAPI.getInstance().getConfigObject(DataConfig.class);
    }

    @Getter
    @Config
    public Object2ObjectOpenHashMap<String, DataSave> garrison = new Object2ObjectOpenHashMap<>();

    @Override
    public EnumFormattingType getFormatType() {
        return EnumFormattingType.YAML;
    }

    @Override
    public File getFile() {
        return new File(
                VelocityFeatures.getFileUtils().getConfigDir("Garrison"),
                "data.yml"
        );
    }

    @Override
    public boolean isStatsConfig() {
        return true;
    }

    public void addToList() {
        String name = GarrisonConfig.getInstance().getGarrison().getName();

        if (!garrison.containsKey(name)) {
            ObjectList<BoostDataSave> boostDataSaveList = new ObjectArrayList<>();

            for (BoostCache boostCache : Garrison.getInstance().boostCaches) {
                boostDataSaveList.add(new BoostDataSave(boostCache.getBoost().getName(), 1, boostCache.getBoost().getMultiplierPerTier(), boostCache.getXp(), boostCache.getXpTillUpgrade()));
            }

            DataSave dataSave = new DataSave("", 0, true, 0, EnumBoostMode.EXP, boostDataSaveList, 0);
            garrison.put(name, dataSave);

            ConfigAPI.getInstance().saveConfig(this);
        }
    }

}
