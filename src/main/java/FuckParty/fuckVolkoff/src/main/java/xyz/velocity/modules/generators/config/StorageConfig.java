package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.generators.config;

import dev.lyons.configapi.ConfigAPI;
import dev.lyons.configapi.ConfigClass;
import dev.lyons.configapi.EnumFormattingType;
import dev.lyons.configapi.annotations.Config;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import lombok.Getter;
import xyz.velocity.VelocityFeatures;
import xyz.velocity.modules.generators.config.saves.GenDataSave;

import java.io.File;
import java.util.ArrayList;

@Config("generatorData")
public class StorageConfig implements ConfigClass {

    public static StorageConfig getInstance() {
        return ConfigAPI.getInstance().getConfigObject(StorageConfig.class);
    }

    @Getter
    @Config
    public Object2ObjectOpenHashMap<String, ArrayList<GenDataSave>> generators = new Object2ObjectOpenHashMap<>();

    @Override
    public EnumFormattingType getFormatType() {
        return EnumFormattingType.YAML;
    }

    @Override
    public File getFile() {
        return new File(
                VelocityFeatures.getFileUtils().getConfigDir("Generator"),
                "data.yml"
        );
    }

    @Override
    public boolean isStatsConfig() {
        return true;
    }

    public void addToData(String id, GenDataSave genDataSave) {
        if (!this.generators.containsKey(id)) {
            generators.put(id, new ArrayList<>());
        }

        generators.get(id).add(genDataSave);
    }

    public boolean hasFaction(String id) {
        return this.generators.containsKey(id);
    }

    public void removeGenerator(String id, GenDataSave genDataSave) {
        if (!generators.containsKey(id)) return;
        generators.get(id).remove(genDataSave);
    }

}
