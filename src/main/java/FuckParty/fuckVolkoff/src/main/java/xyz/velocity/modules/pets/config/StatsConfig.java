package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.pets.config;

import dev.lyons.configapi.ConfigAPI;
import dev.lyons.configapi.ConfigClass;
import dev.lyons.configapi.EnumFormattingType;
import dev.lyons.configapi.annotations.Config;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lombok.Getter;
import org.bukkit.entity.Player;
import xyz.velocity.VelocityFeatures;
import xyz.velocity.modules.pets.config.saves.StatsSave;

import java.io.File;
import java.util.*;

import static xyz.velocity.modules.util.ConfigUtil.saveThread;

@Config("petStatsConfig")
public class StatsConfig implements ConfigClass {

    public static StatsConfig getInstance() {
        return ConfigAPI.getInstance().getConfigObject(StatsConfig.class);
    }

    @Getter
    @Config
    final
    Object2ObjectOpenHashMap<UUID, StatsSave> data = new Object2ObjectOpenHashMap<>();

    @Override
    public EnumFormattingType getFormatType() {
        return EnumFormattingType.JSON;
    }

    @Override
    public File getFile() {
        return new File(
                VelocityFeatures.getFileUtils().getConfigDir("Pets"),
                "stats.json"
        );
    }

    @Override
    public boolean isStatsConfig() {
        return true;
    }

    public StatsSave getPlayerPets(Player player) {

        if (!data.containsKey(player.getUniqueId())) {
            data.put(player.getUniqueId(), new StatsSave(null, 4, new ArrayList<>()));
        }

        //saveData();

        return data.get(player.getUniqueId());

    }

    /*public void saveData() {
        saveThread.submit(this::saveConfig);
    }

    public synchronized void saveConfig() {
        synchronized (this.data) {
            ConfigAPI.getInstance().saveConfig(this);
        }
    }*/

}
