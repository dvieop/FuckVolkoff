package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.customitems.commands;

import org.bukkit.command.CommandSender;
import xyz.velocity.VelocityFeatures;
import xyz.velocity.commands.ChildCommand;
import xyz.velocity.MainConfig;
import xyz.velocity.modules.customitems.config.PartnerItemsConfig;
import xyz.velocity.modules.customitems.config.saves.ItemSave;

import java.util.ArrayList;
import java.util.List;

public class ListSubCommand extends ChildCommand {

    @Override
    protected boolean noChildrenExecute(CommandSender commandSender, String string, String[] args) {

        if (!commandSender.isOp() || !commandSender.hasPermission("customitems.*")) {
            commandSender.sendMessage(VelocityFeatures.chat(MainConfig.getInstance().commandPrefix + "No permission to execute this command."));
            return false;
        }

        List<String> itemList = new ArrayList<>();

        for (ItemSave item : PartnerItemsConfig.getInstance().getItems()) {
            itemList.add(item.getDisplayName() + " &8(&7" + item.getName() + "&8)");
        }

        String msg = MainConfig.getInstance().commandPrefix + "Items list: " + String.join("&7, ", itemList);

        commandSender.sendMessage(VelocityFeatures.chat(msg));
        return false;

    }

    @Override
    public String getName() {
        return "list";
    }

    @Override
    public List<String> getAliases() {
        List<String> aliases = new ArrayList<>();
        aliases.add("list");
        return aliases;
    }

}
