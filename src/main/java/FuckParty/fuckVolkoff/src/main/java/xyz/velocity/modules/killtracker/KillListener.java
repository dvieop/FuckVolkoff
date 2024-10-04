package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.killtracker;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import xyz.velocity.VelocityFeatures;
import xyz.velocity.modules.killtracker.config.KilltrackerConfig;
import xyz.velocity.modules.killtracker.config.StatsConfig;
import xyz.velocity.modules.killtracker.config.StatsSave;
import xyz.velocity.modules.util.ItemUtil;

public class KillListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onDeath(PlayerDeathEvent e) {
        if (!(e.getEntity().getKiller() instanceof Player)) return;
        if (!KilltrackerConfig.getInstance().isEnabled()) return;

        StatsConfig config = StatsConfig.getInstance();

        Player killer = e.getEntity().getKiller();
        Player victim = e.getEntity();

        StatsSave killerStats = config.getStats(killer);
        StatsSave victimStats = config.getStats(victim);

        killerStats.addKill();
        victimStats.addDeath();

        config.getStats().put(killer.getUniqueId(), killerStats);
        config.getStats().put(victim.getUniqueId(), victimStats);

        //config.saveData();
        String item;

        if (ItemUtil.isAirOrNull(killer.getItemInHand())) item = "Fist";
        else if (ItemUtil.hasNoItemMeta(killer.getItemInHand())) item = killer.getItemInHand().getType().name();
        else item = killer.getItemInHand().getItemMeta().getDisplayName();

        Bukkit.broadcastMessage(
                VelocityFeatures.chat(KilltrackerConfig.getInstance().deathMessage
                        .replace("<killer>", killer.getName())
                        .replace("<killerKills>", killerStats.getKills() + "")
                        .replace("<victim>", victim.getName())
                        .replace("<victimKills>", victimStats.getKills() + "")
                        .replace("<item>", item)

                )
        );
    }

    @Getter
    private static KillListener instance;

    public KillListener() {
        instance = this;
    }

}
