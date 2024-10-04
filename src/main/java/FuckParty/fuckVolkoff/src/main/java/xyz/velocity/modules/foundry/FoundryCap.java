package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.foundry;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import xyz.velocity.VelocityFeatures;
import xyz.velocity.modules.foundry.config.FoundryConfig;
import xyz.velocity.modules.foundry.util.Hologram;
import xyz.velocity.modules.foundry.util.Koth;
import xyz.velocity.modules.util.Location;
import xyz.velocity.modules.foundry.util.RewardUtil;

import java.util.*;
import java.util.stream.Collectors;

public class FoundryCap {

    public static boolean isActive = false;

    private Koth koth;
    private RewardUtil reward;

    public FoundryCap(RewardUtil reward) {
        instance = this;

        this.reward = reward;

        FoundryConfig config = FoundryConfig.getInstance();

        Bukkit.broadcastMessage(VelocityFeatures.chat(config.getFoundryStart()
                .replace("<item>", reward.item.getItemMeta().getDisplayName())
        ));

        org.bukkit.Location corner1 = Location.parseToLocation(config.getFoundry().corner1);
        org.bukkit.Location corner2 = Location.parseToLocation(config.getFoundry().corner2);

        koth = new Koth(config.getFoundry().name, reward.capTime, corner1, corner2);
        startFoundry();

        isActive = true;

        Hologram.getInstance().addItem(reward.item);
    }

    @Getter
    private static FoundryCap instance;
    public BukkitTask bukkitTask;

    private void startFoundry() {
        BukkitRunnable bukkitRunnable = new BukkitRunnable() {

            @Override
            public void run() {

                FoundryConfig config = FoundryConfig.getInstance();
                List<Player> playerList = Location.getPlayersInCuboid(koth.getCorner1(), koth.getCorner2()).stream().filter(z -> !z.isDead() && z.isValid()).collect(Collectors.toList());

                if (isExpired(playerList)) return;

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

                    Bukkit.broadcastMessage(VelocityFeatures.chat(config.getPlayerLeft().replace("<player>", name)));
                }

            }
        };

        bukkitTask = bukkitRunnable.runTaskTimer(VelocityFeatures.getInstance(), 0, 20);
    }

    private boolean isExpired(List<Player> playerList) {
        boolean expired = koth.tickSeconds();
        Hologram.getInstance().updateTime(koth.getSecondsLeft());

        if (expired) {

            if (this.koth.isCapping()) {
                Bukkit.broadcastMessage(VelocityFeatures.chat(FoundryConfig.getInstance().getFoundryEnd()
                        .replace("<player>", this.koth.getPlayerCapping().getName())
                        .replace("<item>", this.reward.item.getItemMeta().getDisplayName())
                ));
            } else {
                Bukkit.broadcastMessage(VelocityFeatures.chat(FoundryConfig.getInstance().getFoundryFail()
                        .replace("<item>", this.reward.item.getItemMeta().getDisplayName())
                ));
            }

            if (!playerList.isEmpty()) {
                try {
                    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), reward.command.replace("<player>", this.koth.getPlayerCapping().getName()));
                } catch (Throwable e) { }
            }

            Hologram.getInstance().updateTime(0);

            isActive = false;
            bukkitTask.cancel();

            return true;
        }

        return false;
    }

}
