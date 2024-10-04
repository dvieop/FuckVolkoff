package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.customitems.commands;

import org.bukkit.command.CommandSender;
import xyz.velocity.VelocityFeatures;
import xyz.velocity.commands.BaseCommand;
import xyz.velocity.commands.annotations.Command;
import xyz.velocity.MainConfig;

@Command(name = "customitems")
public class CustomItemsCommand extends BaseCommand {

    public CustomItemsCommand() {
        super(CustomItemsCommand.class.getAnnotation(Command.class).name());

        this.addChildren(new ListSubCommand());
        this.addChildren(new GiveSubCommand());
        this.addChildren(new ReloadSubCommand());
    }

    @Override
    protected boolean noChildrenExecute(CommandSender commandSender, String string, String[] args) {
        return false;
    }

    @Override
    protected void sendUseMessage(CommandSender sender, String string, String[] args) {
        if (!sender.isOp() || !sender.hasPermission("customitems.*")) {
            sender.sendMessage(VelocityFeatures.chat(MainConfig.getInstance().commandPrefix + "No permission to execute this command."));
            return;
        }

        sender.sendMessage(VelocityFeatures.chat(MainConfig.getInstance().commandPrefix + "/customitems give <player> <item> <amount>"));
        sender.sendMessage(VelocityFeatures.chat(MainConfig.getInstance().commandPrefix + "/customitems list"));
        sender.sendMessage(VelocityFeatures.chat(MainConfig.getInstance().commandPrefix + "/customitems reload"));
    }

}
