package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.mining.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import xyz.velocity.MainConfig;
import xyz.velocity.VelocityFeatures;
import xyz.velocity.commands.ChildCommand;
import xyz.velocity.modules.mining.config.MiningConfig;

import java.util.Arrays;
import java.util.List;

public class OpenMerchantCommand extends ChildCommand {

    @Override
    protected boolean noChildrenExecute(CommandSender commandSender, String string, String[] args) {
        if (!commandSender.isOp() || !commandSender.hasPermission("mining.*")) {
            commandSender.sendMessage(VelocityFeatures.chat(MainConfig.getInstance().commandPrefix + "No permission to execute this command."));
            return false;
        }

        if (args.length < 1) {
            this.sendUseMessage(commandSender, string, args);
            return false;
        }

        Player player = Bukkit.getPlayer(args[0]);

        if (player == null) return false;

        Inventory inv = Bukkit.createInventory(null, MiningConfig.getInstance().invSize, VelocityFeatures.chat(MiningConfig.getInstance().guiName));

        player.openInventory(inv);
        return false;
    }

    @Override
    public String getName() {
        return "openmerchant";
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("openmerchant");
    }

    @Override
    protected void sendUseMessage(CommandSender commandSender, String string, String[] args) {
        commandSender.sendMessage(VelocityFeatures.chat(MainConfig.getInstance().commandPrefix + "/mining openmerchant <player>"));
    }

}
