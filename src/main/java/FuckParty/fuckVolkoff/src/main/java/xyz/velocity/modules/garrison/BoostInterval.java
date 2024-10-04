package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.garrison;

import com.golfing8.kore.FactionsKore;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import xyz.velocity.VelocityFeatures;
import xyz.velocity.modules.garrison.config.GarrisonConfig;
import xyz.velocity.modules.util.CapturePoint;

/*public class BoostInterval {

    public BoostInterval() {
        startInterval();
        instance = this;
    }

    public BukkitTask bukkitTask;

    private void startInterval() {
        BukkitRunnable bukkitRunnable = new BukkitRunnable() {
            @Override
            public void run() {
                GarrisonConfig config = GarrisonConfig.getInstance();
                CapturePoint capturePoint = Garrison.getInstance().capturePoint;
                BoostCache boostCache = Garrison.getInstance().getCurrentBoost();

                if (boostCache == null) return;
                if (capturePoint.isNeutral()) return;

                int time = boostCache.getTime() - 1;

                if (time <= 0) {
                    int tier = boostCache.getTier() + 1;

                    if (tier > boostCache.getBoost().getMaxTier()) return;

                    boostCache.setTier(tier);
                    boostCache.setMultiplier(boostCache.getMultiplier() + boostCache.getBoost().getMultiplierPerTier());
                    boostCache.setTime(boostCache.getBoost().getTierUpgradeTime());

                    if (config.announceUpgrade) {
                        Bukkit.broadcastMessage(VelocityFeatures.chat(config.tierUpgrade
                                .replace("<faction>", FactionsKore.getIntegration().getTagFromId(capturePoint.getFactionOwning()))
                                .replace("<mode>", boostCache.getBoost().getDisplayName())
                                .replace("<tier>", tier + "")
                        ));
                    }

                    Garrison.getInstance().hologram.updateLines();
                    Garrison.getInstance().updateData(capturePoint);
                } else {
                    boostCache.setTime(time);
                    Garrison.getInstance().hologram.updateLines();
                }
            }
        };

        bukkitTask = bukkitRunnable.runTaskTimer(VelocityFeatures.getInstance(), 0, 1200);
    }

    @Getter
    private static BoostInterval instance;

}*/
