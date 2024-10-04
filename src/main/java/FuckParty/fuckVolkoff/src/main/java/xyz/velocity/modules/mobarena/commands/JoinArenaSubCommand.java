package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.mobarena.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import xyz.velocity.VelocityFeatures;
import xyz.velocity.commands.ChildCommand;
import xyz.velocity.modules.mining.config.MiningConfig;
import xyz.velocity.modules.mobarena.ArenaManager;

import java.util.Arrays;
import java.util.List;

public class JoinArenaSubCommand extends ChildCommand {

    @Override
    protected boolean noChildrenExecute(CommandSender commandSender, String string, String[] args) {
        Player player = (Player) commandSender;

        ArenaManager.getInstance().addArenaPlayer(player);
        return false;
    }

    @Override
    public String getName() {
        return "join";
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("join");
    }

    /*@Override
    protected void sendUseMessage(CommandSender commandSender, String string, String[] args) {
        commandSender.sendMessage(VelocityFeatures.chat(MainConfig.getInstance().commandPrefix + "/mining openmerchant <player>"));
    }*/

}