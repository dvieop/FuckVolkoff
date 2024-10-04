package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.pets.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import xyz.velocity.VelocityFeatures;
import xyz.velocity.commands.ChildCommand;
import xyz.velocity.MainConfig;
import xyz.velocity.modules.pets.Pets;

import java.util.Arrays;
import java.util.List;

public class GiveShardSubCommand extends ChildCommand {

    @Override
    protected boolean noChildrenExecute(CommandSender commandSender, String string, String[] args) {
        if (!commandSender.isOp() || !commandSender.hasPermission("pets.*")) {
            commandSender.sendMessage(VelocityFeatures.chat(MainConfig.getInstance().commandPrefix + "No permission to execute this command."));
            return false;
        }

        Player toGive = Bukkit.getPlayer(args[0]);
        double multiplier = Double.parseDouble(args[1]);

        if (toGive != null) {
            ItemStack item = Pets.getInstance().buildItem("xpshard", 1, multiplier);

            if (item == null) return false;

            toGive.getInventory().addItem(item);
        }

        return false;
    }

    @Override
    public String getName() {
        return "giveshard";
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("giveshard");
    }

}
