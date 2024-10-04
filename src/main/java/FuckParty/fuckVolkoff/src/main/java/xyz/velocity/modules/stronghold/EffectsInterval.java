package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.stronghold;

import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import xyz.velocity.VelocityFeatures;
import xyz.velocity.modules.util.CapturePoint;

public class EffectsInterval {

    public EffectsInterval(CapturePoint capturePoint) {
        instance = this;
        startEffectsInterval(15, capturePoint);
    }

    public BukkitTask bukkitTask;

    private void startEffectsInterval(int interval, CapturePoint capturePoint) {
        BukkitRunnable bukkitRunnable = new BukkitRunnable() {
            @Override
            public void run() {
                if (capturePoint.isNeutral()) return;
                Stronghold.getInstance().giveEffects(capturePoint);
            }
        };

        bukkitTask = bukkitRunnable.runTaskTimer(VelocityFeatures.getInstance(), 0, interval * 20);
    }

    private static EffectsInterval instance;

    public static EffectsInterval getInstance() {
        return instance;
    }

}
