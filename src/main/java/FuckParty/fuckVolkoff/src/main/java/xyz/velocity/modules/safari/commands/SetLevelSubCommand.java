package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.safari.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.velocity.MainConfig;
import xyz.velocity.VelocityFeatures;
import xyz.velocity.commands.ChildCommand;
import xyz.velocity.modules.safari.config.StatsConfig;
import xyz.velocity.modules.safari.config.saves.StatsSave;

import java.util.Arrays;
import java.util.List;

public class SetLevelSubCommand extends ChildCommand {

    @Override
    protected boolean noChildrenExecute(CommandSender commandSender, String string, String[] args) {
        if (!commandSender.isOp() || !commandSender.hasPermission("safari.*")) {
            commandSender.sendMessage(VelocityFeatures.chat(MainConfig.getInstance().commandPrefix + "No permission to execute this command."));
            return false;
        }

        if (args.length < 1) {
            this.sendUseMessage(commandSender, string, args);
            return false;
        }

        Player player = Bukkit.getPlayer(args[0]);
        int level = Integer.parseInt(args[1]);

        if (player == null) return false;

        StatsSave statsSave = StatsConfig.getInstance().getPlayerStats(player);

        statsSave.setLevel(level);

        StatsConfig.getInstance().getData().put(player.getUniqueId(), statsSave);
        StatsConfig.getInstance().saveData();

        return false;
    }

    @Override
    public String getName() {
        return "set";
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("set");
    }

    @Override
    protected void sendUseMessage(CommandSender commandSender, String string, String[] args) {
        commandSender.sendMessage(VelocityFeatures.chat(MainConfig.getInstance().commandPrefix + "/safari set <player> <level>"));
    }

}
