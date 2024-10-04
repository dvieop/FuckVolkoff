package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.waterpearl;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lombok.Getter;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import xyz.velocity.VelocityFeatures;

import java.util.UUID;

public class PearlListener implements Listener {

    public static Object2ObjectOpenHashMap<UUID, BukkitRunnable> pearls = new Object2ObjectOpenHashMap<>();

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPearlThrow(ProjectileLaunchEvent e) {

        if (!(e.getEntity() instanceof EnderPearl)) return;
        if (!(e.getEntity().getShooter() instanceof Player)) return;

        Vector vector = e.getEntity().getVelocity();

        BukkitRunnable bukkitRunnable = new BukkitRunnable() {

            @Override
            public void run() {
                double newY = vector.getY();
                double newX = vector.getX();
                double newZ = vector.getZ();

                newY *= 0.9900000095367432D;
                newX *= 0.9900000095367432D;
                newZ *= 0.9900000095367432D;
                newY -= 0.03F;

                vector.setY(newY);
                vector.setX(newX);
                vector.setZ(newZ);

                e.getEntity().setVelocity(vector);
            }

        };

        bukkitRunnable.runTaskTimer(VelocityFeatures.getInstance(), 1l, 1l);

        pearls.put(e.getEntity().getUniqueId(), bukkitRunnable);

    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onLand(ProjectileHitEvent e) {

        if (!(e.getEntity() instanceof EnderPearl)) return;
        if (!(e.getEntity().getShooter() instanceof Player)) return;

        if (pearls.containsKey(e.getEntity().getUniqueId())) {
            pearls.get(e.getEntity().getUniqueId()).cancel();
            pearls.remove(e.getEntity().getUniqueId());
        }

    }

    @Getter
    private static PearlListener instance;

    public PearlListener() {
        instance = this;
    }

}
