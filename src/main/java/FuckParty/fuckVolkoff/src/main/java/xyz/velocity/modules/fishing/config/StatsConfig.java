package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.fishing.config;

import dev.lyons.configapi.ConfigAPI;
import dev.lyons.configapi.ConfigClass;
import dev.lyons.configapi.EnumFormattingType;
import dev.lyons.configapi.annotations.Config;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lombok.Getter;
import org.bukkit.entity.Player;
import xyz.velocity.VelocityFeatures;
import xyz.velocity.modules.util.ConfigUtil;

import java.io.File;
import java.util.UUID;

@Config("fishingStats")
public class StatsConfig implements ConfigClass {

    public static StatsConfig getInstance() {
        return ConfigAPI.getInstance().getConfigObject(StatsConfig.class);
    }

    @Getter
    @Config
    final
    Object2ObjectOpenHashMap<UUID, Integer> data = new Object2ObjectOpenHashMap<>();

    @Override
    public EnumFormattingType getFormatType() {
        return EnumFormattingType.YAML;
    }

    @Override
    public File getFile() {
        return new File(
                VelocityFeatures.getFileUtils().getConfigDir("Fishing"),
                "Stats.yml"
        );
    }

    @Override
    public boolean isStatsConfig() {
        return true;
    }

    public int getPlayerStats(Player player) {

        if (!data.containsKey(player.getUniqueId())) {
            data.put(player.getUniqueId(), 0);
        }

        //saveData();

        return data.get(player.getUniqueId());

    }

    /*public void saveData() {
        ConfigUtil.saveThread.submit(this::saveConfig);
    }

    public synchronized void saveConfig() {
        synchronized (this.data) {
            ConfigAPI.getInstance().saveConfig(this);
        }
    }*/

}
