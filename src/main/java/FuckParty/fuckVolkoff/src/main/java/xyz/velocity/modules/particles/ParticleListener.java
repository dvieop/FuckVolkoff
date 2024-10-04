package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.particles;

import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import java.util.UUID;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.velocity.VelocityFeatures;
import xyz.velocity.modules.particles.config.ParticleConfig;

public class ParticleListener implements Listener {

  @EventHandler
  public void onJoin(PlayerJoinEvent e) {
    Player p = e.getPlayer();

    ParticleConfig.getInstance().toggles.computeIfAbsent(p.getUniqueId(), t -> true);
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onDamage(EntityDamageByEntityEvent e) {
    if (e.isCancelled()) {
      return;
    }
    if (!(e.getEntity() instanceof Player)) {
      return;
    }
    if (!(e.getDamager() instanceof Player)) {
      return;
    }

    if (!ParticleConfig.getInstance().toggles.containsKey(e.getDamager().getUniqueId())) {
      return;
    }
    if (!ParticleConfig.getInstance().toggles.get(e.getDamager().getUniqueId())) {
      return;
    }

    Player victim = (Player) e.getEntity();
    Player attacker = (Player) e.getDamager();

    double damage = Particles.getInstance().round(e.getDamage(), 2);

    Hologram hologram = DHAPI.createHologram(UUID.randomUUID().toString(),
        victim.getLocation().add(0, 2, 0));

    DHAPI.addHologramLine(hologram, VelocityFeatures.chat(ParticleConfig.getInstance().damageHolo
        .replace("<damage>", String.valueOf(damage))
    ));

    double x = victim.getLocation().getX() + Particles.getInstance().getRandom(-1, 1);
    double y = victim.getLocation().getY() + Particles.getInstance().getRandom(1, 2);
    double z = victim.getLocation().getZ() + Particles.getInstance().getRandom(-1, 1);

    Location loc = new Location(victim.getWorld(), x, y, z);

    DHAPI.moveHologram(hologram, loc);

    hologram.setDefaultVisibleState(false);
    hologram.setShowPlayer(attacker);

    new BukkitRunnable() {

      public void run() {
        hologram.delete();
      }

    }.runTaskLater(VelocityFeatures.getInstance(), 20);

  }

  @Getter
  private static ParticleListener instance;

  public ParticleListener() {
    instance = this;
  }

}
