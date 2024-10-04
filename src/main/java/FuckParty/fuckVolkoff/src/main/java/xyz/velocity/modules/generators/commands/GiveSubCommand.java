package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.generators.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import xyz.velocity.MainConfig;
import xyz.velocity.VelocityFeatures;
import xyz.velocity.commands.ChildCommand;
import xyz.velocity.modules.fishing.Fishing;
import xyz.velocity.modules.generators.Generator;

import java.util.Arrays;
import java.util.List;

public class GiveSubCommand extends ChildCommand {


    @Override
    protected boolean noChildrenExecute(CommandSender commandSender, String string, String[] args) {
        if (!commandSender.isOp() || !commandSender.hasPermission("generator.*")) {
            commandSender.sendMessage(VelocityFeatures.chat(MainConfig.getInstance().commandPrefix + "No permission to execute this command."));
            return false;
        }

        if (args.length < 2) {
            this.sendUseMessage(commandSender, string, args);
            return false;
        }

        Player player = Bukkit.getPlayer(args[0]);
        String type = args[1];
        int tier = 1;

        if (player == null) return false;

        if (args.length > 2 && args[2] != null) {
            tier = Integer.parseInt(args[2]);
        }

        ItemStack item = Generator.getInstance().buildItem(type, tier);
        player.getInventory().addItem(item);

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
        commandSender.sendMessage(VelocityFeatures.chat(MainConfig.getInstance().commandPrefix + "/generator give <player> <type> [tier]"));
    }

}
