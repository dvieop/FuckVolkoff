package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.tntpouch.commands;

import org.bukkit.command.CommandSender;
import xyz.velocity.VelocityFeatures;
import xyz.velocity.commands.BaseCommand;
import xyz.velocity.commands.annotations.Command;
import xyz.velocity.MainConfig;

@Command(name = "tntpouch")
public class PouchCommand extends BaseCommand {

    public PouchCommand() {
        super(PouchCommand.class.getAnnotation(Command.class).name());

        this.addChildren(new GiveSubCommand());
        this.addChildren(new WithdrawSubCommand());
        this.addChildren(new ReloadSubCommand());
    }

    @Override
    protected boolean noChildrenExecute(CommandSender commandSender, String string, String[] args) {
        return false;
    }

    @Override
    protected void sendUseMessage(CommandSender commandSender, String string, String[] args) {
        if (!commandSender.isOp() || !commandSender.hasPermission("tntpouch.*")) {
            commandSender.sendMessage(VelocityFeatures.chat(MainConfig.getInstance().commandPrefix + "/tntpouch withdraw [amount]"));
            return;
        }

        commandSender.sendMessage(VelocityFeatures.chat(MainConfig.getInstance().commandPrefix + "/tntpouch give <player> <tier>"));
        commandSender.sendMessage(VelocityFeatures.chat(MainConfig.getInstance().commandPrefix + "/tntpouch withdraw [amount]"));
        commandSender.sendMessage(VelocityFeatures.chat(MainConfig.getInstance().commandPrefix + "/tntpouch fill <amount>"));
        commandSender.sendMessage(VelocityFeatures.chat(MainConfig.getInstance().commandPrefix + "/tntpouch reload"));
    }

}