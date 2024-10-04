package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.safari.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.velocity.VelocityFeatures;
import xyz.velocity.commands.ChildCommand;
import xyz.velocity.MainConfig;
import xyz.velocity.modules.safari.Safari;
import xyz.velocity.modules.safari.config.SafariConfig;
import xyz.velocity.modules.util.Location;

import java.util.Arrays;
import java.util.List;

public class LeaveSubCommand extends ChildCommand {

    @Override
    protected boolean noChildrenExecute(CommandSender commandSender, String string, String[] args) {
        Player player = (Player) commandSender;

        List<String> worlds = SafariConfig.getInstance().getWhitelistedWorlds();

        if (!worlds.contains(player.getWorld().getName())) {
            player.sendMessage(VelocityFeatures.chat(SafariConfig.getInstance().cantLeave));
            return false;
        }

        //Safari.getInstance().updateInventory(player);

        player.teleport(Location.parseToLocation(SafariConfig.getInstance().leaveLocation));

        return false;
    }

    @Override
    public String getName() {
        return "leave";
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("leave");
    }

    @Override
    protected void sendUseMessage(CommandSender commandSender, String string, String[] args) {
        commandSender.sendMessage(VelocityFeatures.chat(MainConfig.getInstance().commandPrefix + "/safari leave"));
    }

}
