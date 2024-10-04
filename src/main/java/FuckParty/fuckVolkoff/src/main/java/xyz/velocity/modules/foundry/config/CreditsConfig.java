package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.foundry.config;

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

@Config("creditsConfig")
public class CreditsConfig implements ConfigClass {

    public static CreditsConfig getInstance() {
        return ConfigAPI.getInstance().getConfigObject(CreditsConfig.class);
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
                VelocityFeatures.getFileUtils().getConfigDir("Foundry"),
                "CreditsData.yml"
        );
    }

    @Override
    public boolean isStatsConfig() {
        return true;
    }

    public int getPlayerCredits(Player player) {

        if (!data.containsKey(player.getUniqueId())) {
            data.put(player.getUniqueId(), 0);
        }

        //saveData();

        return data.get(player.getUniqueId());

    }

    //public void saveData() {
        //saveThread.submit(this::saveConfig);
    //}

    /*public synchronized void saveConfig() {
        synchronized (this.data) {
            ConfigAPI.getInstance().saveConfig(this);
        }
    }*/

}
