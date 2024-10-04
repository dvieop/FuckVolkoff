package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.fishing.commands;

import org.bukkit.command.CommandSender;
import xyz.velocity.VelocityFeatures;
import xyz.velocity.commands.BaseCommand;
import xyz.velocity.commands.annotations.Command;
import xyz.velocity.MainConfig;

@Command(name = "fishing")
public class FishingCommand extends BaseCommand {

    public FishingCommand() {
        super(FishingCommand.class.getAnnotation(Command.class).name());

        this.addChildren(new GiveSubCommand());
        this.addChildren(new ReloadSubCommand());
        this.addChildren(new OpenMerchantCommand());
    }

    @Override
    protected boolean noChildrenExecute(CommandSender commandSender, String string, String[] args) {
        return false;
    }

    @Override
    protected void sendUseMessage(CommandSender commandSender, String string, String[] args) {
        if (!commandSender.isOp() || !commandSender.hasPermission("fishing.*")) {
            commandSender.sendMessage(VelocityFeatures.chat(MainConfig.getInstance().commandPrefix + "No permission to execute this command."));
            return;
        }

        commandSender.sendMessage(VelocityFeatures.chat(MainConfig.getInstance().commandPrefix + "/fishing give <player>"));
        commandSender.sendMessage(VelocityFeatures.chat(MainConfig.getInstance().commandPrefix + "/fishing openmerchant <player>"));
        commandSender.sendMessage(VelocityFeatures.chat(MainConfig.getInstance().commandPrefix + "/fishing reload"));
    }

}
