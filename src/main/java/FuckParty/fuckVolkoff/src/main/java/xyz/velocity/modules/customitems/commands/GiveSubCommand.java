package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.customitems.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import xyz.velocity.VelocityFeatures;
import xyz.velocity.commands.ChildCommand;
import xyz.velocity.MainConfig;
import xyz.velocity.modules.customitems.PartnerItems;

import java.util.ArrayList;
import java.util.List;

public class GiveSubCommand extends ChildCommand {

    @Override
    protected boolean noChildrenExecute(CommandSender commandSender, String string, String[] args) {

        if (!commandSender.isOp() || !commandSender.hasPermission("customitems.*")) {
            commandSender.sendMessage(VelocityFeatures.chat(MainConfig.getInstance().commandPrefix + "No permission to execute this command."));
            return false;
        }

        if (args.length < 3) {
            this.sendUseMessage(commandSender, string, args);
            return false;
        }

        Player p = Bukkit.getPlayer(args[0]);
        String item = args[1];
        int amount;

        try {
            amount = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return false;
        }

        if (p != null && item != null) {
            ItemStack itemStack = PartnerItems.getInstance().buildItem(item, amount);

            if (itemStack == null) return false;

            p.getInventory().addItem(PartnerItems.getInstance().addGlow(itemStack));
        }

        return false;

    }

    @Override
    public String getName() {
        return "give";
    }

    @Override
    public List<String> getAliases() {
        List<String> aliases = new ArrayList<>();
        aliases.add("give");
        return aliases;
    }

    @Override
    protected void sendUseMessage(CommandSender commandSender, String string, String[] args) {
        commandSender.sendMessage(VelocityFeatures.chat(MainConfig.getInstance().commandPrefix + "/customitems give <player> <item> <amount>"));
    }

}
