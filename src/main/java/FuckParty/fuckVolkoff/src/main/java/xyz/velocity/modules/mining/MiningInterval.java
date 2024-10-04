package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.mining;

import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import xyz.velocity.VelocityFeatures;

public class MiningInterval {

    public MiningInterval() {
        startInterval();
        instance = this;
    }

    public BukkitTask bukkitTask;

    private void startInterval() {
        BukkitRunnable bukkitRunnable = new BukkitRunnable() {

            @Override
            public void run() {
                for (OreCache value : Mining.miningBlocks.values()) {
                    if (value.isOnCooldown()) continue;

                    Material toSet = Material.getMaterial(value.getTypeSave().getMaterial());
                    Block block = value.getLocation().getBlock();

                    if (block.getType() != toSet) block.setType(toSet);
                }
            }
        };

        bukkitTask = bukkitRunnable.runTaskTimer(VelocityFeatures.getInstance(), 0, 100);
    }

    @Getter
    private static MiningInterval instance;

}
