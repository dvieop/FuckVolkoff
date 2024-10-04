package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.mobarena;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.velocity.VelocityFeatures;
import xyz.velocity.modules.mobarena.config.MobArenaConfig;
import xyz.velocity.modules.mobarena.config.saves.MobArenaSave;
import xyz.velocity.modules.util.Location;

import java.time.ZoneId;
import java.time.ZonedDateTime;

public class ArenaManager {

    private static final ObjectList<ArenaPlayer> arenaPlayers = new ObjectArrayList<>();

    MobArenaConfig config = MobArenaConfig.getInstance();
    MobArenaSave mobArena = config.getArenas();

    private boolean active = false;

    int round;
    int phase;
    int time;

    ZoneId zoneId;

    int scheduleTask;

    @Getter
    public static ArenaManager instance;

    public ArenaManager() {
        this.round = 1;
        this.phase = 1;
        this.zoneId = ZoneId.of(config.getSchedule().getTimezone());

        scheduleTimer();

        instance = this;
    }

    public ArenaPlayer getArenaPlayer(Player player) {
        return arenaPlayers.stream().filter(obj -> obj.getPlayer().equals(player.getUniqueId())).findFirst().get();
    }

    public void addArenaPlayer(Player player) {
        if (!active) {
            player.sendMessage(VelocityFeatures.chat(MobArenaConfig.getInstance().arenaNotActive));
            return;
        }
        if (arenaPlayers.size() >= mobArena.getMaxPlayers()) {
            player.sendMessage(VelocityFeatures.chat(MobArenaConfig.getInstance().arenaFull));
            return;
        }

        player.teleport(Location.parseToLocation(MobArenaConfig.getInstance().lobbyLocation));
        player.sendMessage(VelocityFeatures.chat(MobArenaConfig.getInstance().playerJoin));
        MobArena.getInstance().addInvContents(player);

        arenaPlayers.add(new ArenaPlayer(player.getUniqueId()));
    }

    public boolean isReady(int[] time){
        for (String string : config.getSchedule().getSchedules()){
            String[] split = string.split(":");

            int hour = Integer.parseInt(split[0]);
            int minute = Integer.parseInt(split[1]);

            if (hour == time[0] && minute == time[1]) return true;
        }

        return false;
    }

    public void sendArenaStartMessage() {
        Bukkit.broadcastMessage(VelocityFeatures.chat(config.arenaStart));
    }

    public void cancelTask() {
        Bukkit.getScheduler().cancelTask(this.scheduleTask);
    }

    private void scheduleTimer() {
        scheduleTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (active) return;

                ZonedDateTime zonedDateTime = ZonedDateTime.now(zoneId);

                if (isReady(new int[] {zonedDateTime.getHour(), zonedDateTime.getMinute()})) {
                    active = true;
                    //start arena method here
                    sendArenaStartMessage();
                }
            }
        }.runTaskTimer(VelocityFeatures.getInstance(), 0, 20).getTaskId();
    }

}
