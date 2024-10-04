package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.stronghold;

import com.golfing8.kore.FactionsKore;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import xyz.velocity.VelocityFeatures;
import xyz.velocity.modules.util.CapturePoint;

public class CommandsInterval {

    public CommandsInterval() {
        startCommandsInterval();
        instance = this;
    }

    public BukkitTask bukkitTask;

    private void startCommandsInterval() {
        BukkitRunnable bukkitRunnable = new BukkitRunnable() {
            @Override
            public void run() {

                for (CapturePoint capturePoint : Stronghold.getInstance().capturePoints) {
                    if (capturePoint.isNeutral()) continue;

                    Object2ObjectMap<String, Integer> commands = capturePoint.getCommands();

                    for (String command : commands.keySet()) {
                        int time = commands.get(command) - 1;

                        if (time <= 0) {
                            try {
                                if (command.contains("<leader>")) {
                                    OfflinePlayer getLeader = FactionsKore.getIntegration().getOfflineLeader(capturePoint.getFactionOwning());
                                    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command.replace("<leader>", getLeader.getName()));
                                } else if (command.contains("<all>")) {
                                    for (Player onlineMember : FactionsKore.getIntegration().getOnlineMembers(capturePoint.getFactionOwning())) {
                                        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command.replace("<all>", onlineMember.getName()));
                                    }
                                }
                            } catch (Throwable e) { }

                            capturePoint.resetCommand(command);
                        } else {
                            commands.replace(command, time);
                        }
                    }
                }
            }
        };

        bukkitTask = bukkitRunnable.runTaskTimer(VelocityFeatures.getInstance(), 0, 1200);
    }

    private static CommandsInterval instance;
    public static CommandsInterval getInstance() {
        return instance;
    }


}
