package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.infinitekoth;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import xyz.velocity.VelocityFeatures;
import xyz.velocity.modules.infinitekoth.config.KothConfig;
import xyz.velocity.modules.infinitekoth.util.Hologram;
import xyz.velocity.modules.infinitekoth.util.Koth;
import xyz.velocity.modules.util.Location;

import java.util.*;
import java.util.stream.Collectors;

public class KothCap {

    public static Koth koth;
    public BukkitTask bukkitTask;
    @Getter
    private static KothCap instance;

    public KothCap() {
        KothConfig config = KothConfig.getInstance();

        org.bukkit.Location corner1 = Location.parseToLocation(config.getLocation1());
        org.bukkit.Location corner2 = Location.parseToLocation(config.getLocation2());

        koth = new Koth("Infinite Koth", config.getRewardInterval(), corner1, corner2);
        startKoth();

        instance = this;
    }

    private void startKoth() {
        BukkitRunnable bukkitRunnable = new BukkitRunnable() {

            @Override
            public void run() {

                KothConfig config = KothConfig.getInstance();
                List<Player> playerList = Location.getPlayersInCuboid(koth.getCorner1(), koth.getCorner2()).stream().filter(z -> !z.isDead() && z.isValid()).collect(Collectors.toList());

                if (!playerList.isEmpty()) {
                    tickSeconds(playerList);
                }

                if (!koth.isCapping()) {
                    if (playerList.isEmpty()) return;

                    Player player = playerList.get(0);

                    koth.setPlayerCapping(player);

                    Bukkit.broadcastMessage(VelocityFeatures.chat(config.getPlayerEnter().replace("<player>", player.getName())));

                    return;
                }

                if (!playerList.contains(koth.getPlayerCapping())) {

                    String name = koth.getPlayerCapping().getName();

                    koth.setPlayerCapping(null);
                    koth.resetTime(KothConfig.getInstance().getRewardInterval());

                    Bukkit.broadcastMessage(VelocityFeatures.chat(config.getPlayerLeft().replace("<player>", name)));
                }

            }
        };

        bukkitTask = bukkitRunnable.runTaskTimer(VelocityFeatures.getInstance(), 0, 20);
    }

    private boolean tickSeconds(List<Player> playerList) {
        boolean expired = koth.tickSeconds();

        Hologram.getInstance().updateHologram(koth.getSeconds(), koth.getTotalCaptureTime(), koth.getCurrentTier(), playerList.get(0).getName());
        remindMessage();

        if (expired) {

            if (this.koth.isCapping()) {

                Map<String,String> rewardList = getRewardList();

                rewardList.forEach((key, value) -> {
                    sendMessage(KothConfig.getInstance().getGainReward(), "<player>", koth.getPlayerCapping().getName(), "<reward>", key, false);
                    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), value.replace("<player>", koth.getPlayerCapping().getName()));
                });
            }

            koth.setSeconds(KothConfig.getInstance().getRewardInterval());

            return true;
        }

        koth.setCurrentTier(updateTier(koth.getTotalCaptureTime(), koth.getCurrentTier()));

        return false;
    }

    private int getMaxTiers() {
        return KothConfig.getInstance().getReward().tiers.size();
    }

    private int getTierUpgradeTime() {
        return KothConfig.getInstance().getTierUpgradeAfter();
    }

    private int updateTier(int totalSeconds, int currentTier) {
        int maxTiers = getMaxTiers();
        int upgradeAt = getTierUpgradeTime() * currentTier;

        if (totalSeconds >= upgradeAt) {
            if (currentTier >= maxTiers) {
                return maxTiers;
            }

            currentTier++;

            sendMessage(KothConfig.getInstance().getTierUpgrade(), "<player>", koth.getPlayerCapping().getName(), "<tier>", currentTier + "", true);
            return currentTier++;
        }

        return currentTier;
    }

    private void sendMessage(String s, String p1, String r1, String p2, String r2, boolean broadcast) {

        String replace = s
                .replace(p1, r1)
                .replace(p2, r2);

        if (broadcast) {
            Bukkit.broadcastMessage(VelocityFeatures.chat(replace));
        } else {
            koth.getPlayerCapping().sendMessage(VelocityFeatures.chat(replace));
        }

    }

    private Map getRewardList() {

        Map<String, String> rewards = new HashMap();
        JsonArray list = KothConfig.getInstance().getReward().tiers;

        list.forEach(tier -> {
            JsonObject obj = tier.getAsJsonObject();

            if (obj.get("tier").getAsInt() == koth.getCurrentTier()) {

                JsonArray array = obj.get("rewards").getAsJsonArray();

                array.forEach(reward -> {
                    JsonObject rewardObj = reward.getAsJsonObject();

                    rewards.put(rewardObj.get("name").getAsString(), rewardObj.get("command").getAsString());
                });
            }

        });

        return rewards;

    }

    private void remindMessage() {

        KothConfig config = KothConfig.getInstance();

        if (koth.getReminderInterval() >= config.getRemindInterval()) {

            Bukkit.broadcastMessage(VelocityFeatures.chat(config.getKothReminder()
                    .replace("<player>", koth.getPlayerCapping().getName())
                    .replace("<total_time>", formatTime(koth.getTotalCaptureTime()))
                    .replace("<tier>", koth.getCurrentTier() + "")
            ));

            koth.setReminderInterval(0);

        }

    }

    private String formatTime(int n) {
        String time = "";

        int hours = n / 3600;
        int minutes = (n % 3600) / 60;
        n = n % 60;

        time += hours > 0 ? hours + "h " : "";

        return time += minutes + "m " + n + "s";
    }

}
