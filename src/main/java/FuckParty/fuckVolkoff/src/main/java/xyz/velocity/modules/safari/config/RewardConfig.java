package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.safari.config;

import dev.lyons.configapi.ConfigAPI;
import dev.lyons.configapi.ConfigClass;
import dev.lyons.configapi.EnumFormattingType;
import dev.lyons.configapi.annotations.Config;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lombok.Getter;
import org.bukkit.entity.Player;
import xyz.velocity.VelocityFeatures;
import xyz.velocity.modules.safari.config.saves.SpecialRewardSave;

import java.io.File;
import java.util.*;

import static xyz.velocity.modules.util.ConfigUtil.saveThread;

@Config("rewardSaves")
public class RewardConfig implements ConfigClass {

    public static RewardConfig getInstance() {
        return ConfigAPI.getInstance().getConfigObject(RewardConfig.class);
    }

    /*@Getter
    @Config
    final
    Object2ObjectOpenHashMap<UUID, List<SpecialRewardSave>> rewardCache = new Object2ObjectOpenHashMap<>();*/

    @Override
    public EnumFormattingType getFormatType() {
        return EnumFormattingType.YAML;
    }

    @Override
    public File getFile() {
        return new File(
                VelocityFeatures.getFileUtils().getConfigDir("Safari"),
                "rewardStorage.yml"
        );
    }

    @Override
    public boolean isStatsConfig() {
        return true;
    }

    /*public List<SpecialRewardSave> getPlayerRewards(Player player) {

        if (!rewardCache.containsKey(player.getUniqueId())) {
            rewardCache.put(player.getUniqueId(), new ArrayList<>());
        }

        saveData();

        return rewardCache.get(player.getUniqueId());

    }

    public void updatePlayerRewards(Player player, List<SpecialRewardSave> rewardList) {
        rewardCache.put(player.getUniqueId(), rewardList);
        saveData();
    }

    public void saveData() {
        saveThread.submit(this::saveConfig);
    }

    public synchronized void saveConfig() {
        synchronized (this.rewardCache) {
            ConfigAPI.getInstance().saveConfig(this);
        }
    }*/

}
