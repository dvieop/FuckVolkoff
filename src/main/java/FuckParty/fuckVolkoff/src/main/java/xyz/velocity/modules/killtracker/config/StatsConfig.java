package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.killtracker.config;

import dev.lyons.configapi.ConfigAPI;
import dev.lyons.configapi.ConfigClass;
import dev.lyons.configapi.EnumFormattingType;
import dev.lyons.configapi.annotations.Config;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lombok.Getter;
import org.bukkit.entity.Player;
import xyz.velocity.VelocityFeatures;

import java.io.File;
import java.util.UUID;

import static xyz.velocity.modules.util.ConfigUtil.saveThread;

@Config("killTrackerStats")
public class StatsConfig implements ConfigClass {

    public static StatsConfig getInstance() {
        return ConfigAPI.getInstance().getConfigObject(StatsConfig.class);
    }

    @Getter
    @Config
    final
    Object2ObjectOpenHashMap<UUID, StatsSave> stats = new Object2ObjectOpenHashMap<>();

    @Override
    public EnumFormattingType getFormatType() {
        return EnumFormattingType.YAML;
    }

    @Override
    public File getFile() {
        return new File(
                VelocityFeatures.getFileUtils().getConfigDir("KillTracker"),
                "stats.yml"
        );
    }

    @Override
    public boolean isStatsConfig() {
        return true;
    }

    public StatsSave getStats(Player player) {

        if (!stats.containsKey(player.getUniqueId())) {
            stats.put(player.getUniqueId(), new StatsSave(0, 0));
        }

        //saveData();

        return stats.get(player.getUniqueId());

    }

    /*public void saveData() {
        saveThread.submit(this::saveConfig);
    }

    public synchronized void saveConfig() {
        synchronized (this.stats) {
            ConfigAPI.getInstance().saveConfig(this);
        }
    }*/
}
