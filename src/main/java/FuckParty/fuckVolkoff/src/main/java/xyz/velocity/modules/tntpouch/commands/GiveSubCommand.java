package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.tntpouch.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.velocity.VelocityFeatures;
import xyz.velocity.commands.ChildCommand;
import xyz.velocity.MainConfig;
import xyz.velocity.modules.tntpouch.TNTPouch;

import java.util.Arrays;
import java.util.List;

public class GiveSubCommand extends ChildCommand {

    @Override
    protected boolean noChildrenExecute(CommandSender commandSender, String string, String[] args) {
        if (!commandSender.isOp() || !commandSender.hasPermission("tntpouch.*")) {
            commandSender.sendMessage(VelocityFeatures.chat(MainConfig.getInstance().commandPrefix + "No permission to execute this command."));
            return false;
        }

        if (args.length < 2) {
            this.sendUseMessage(commandSender, string, args);
            return false;
        }

        Player player = Bukkit.getPlayer(args[0]);
        int tier = Integer.parseInt(args[1]);

        if (player == null) return false;

        TNTPouch.getInstance().createTNTPouch(player, tier);

        return false;
    }

    @Override
    public String getName() {
        return "give";
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("give");
    }

    @Override
    protected void sendUseMessage(CommandSender commandSender, String string, String[] args) {
        commandSender.sendMessage(VelocityFeatures.chat(MainConfig.getInstance().commandPrefix + "/tntpouch give <player> <tier>"));
    }

}
