package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.safari;

import lombok.Getter;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import xyz.velocity.VelocityFeatures;

public class SafariInterval {

    public SafariInterval() {
        startInterval();
        instance = this;
    }

    public BukkitTask bukkitTask;

    private void startInterval() {
        BukkitRunnable bukkitRunnable = new BukkitRunnable() {

            @Override
            public void run() {
                for (SafariCache safariCache : Safari.safariCache) {
                    if (!safariCache.cooldownExpired()) {
                        safariCache.hologram.updateHologram("cooldown");
                    } else {
                        if (!safariCache.isActive && !safariCache.rewardAvailable) {
                            safariCache.hologram.updateHologram("available");
                        }
                    }

                    if (safariCache.rewardAvailable || safariCache.isActive) {
                        if (safariCache.tickReset()) {
                            safariCache.setActive(false);
                            safariCache.setRewardAvailable(false);
                            safariCache.setStarter(null);
                            safariCache.setCooldown(System.currentTimeMillis() + safariCache.safariTierSave.getCooldown() * 1000L);
                            safariCache.setResetTimer(60);

                            safariCache.hologram.updateHologram("cooldown");

                            for (LivingEntity mobEntity : safariCache.mobEntities) {
                                if (!mobEntity.isDead()) {
                                    mobEntity.remove();
                                }
                            }

                            safariCache.mobEntities.clear();
                        }
                    }
                }
            }
        };

        bukkitTask = bukkitRunnable.runTaskTimer(VelocityFeatures.getInstance(), 0, 100);
    }

    @Getter
    private static SafariInterval instance;

}
