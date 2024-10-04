package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.armorsets.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.velocity.VelocityFeatures;
import xyz.velocity.commands.ChildCommand;
import xyz.velocity.MainConfig;

import java.util.Arrays;
import java.util.List;

public class SetHpSubCommand extends ChildCommand {

    @Override
    protected boolean noChildrenExecute(CommandSender commandSender, String string, String[] args) {
        if (!commandSender.isOp() || !commandSender.hasPermission("armorsets.*")) {
            commandSender.sendMessage(VelocityFeatures.chat(MainConfig.getInstance().commandPrefix + "No permission to execute this command."));
            return false;
        }

        if (args.length < 2) return false;

        Player p = Bukkit.getPlayer(args[0]);
        double hp = Double.parseDouble(args[1]);

        if (p == null) return false;

        p.setMaxHealth(hp);
        return false;
    }

    @Override
    public String getName() {
        return "sethp";
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("sethp");
    }

}
