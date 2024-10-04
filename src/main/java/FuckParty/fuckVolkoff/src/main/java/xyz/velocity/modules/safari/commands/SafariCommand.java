package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.safari.commands;

import org.bukkit.command.CommandSender;
import xyz.velocity.VelocityFeatures;
import xyz.velocity.commands.BaseCommand;
import xyz.velocity.commands.annotations.Command;
import xyz.velocity.MainConfig;

@Command(name = "safari")
public class SafariCommand extends BaseCommand {

    public SafariCommand() {
        super(SafariCommand.class.getAnnotation(Command.class).name());

        this.addChildren(new GiveSubCommand());
        this.addChildren(new ReloadSubCommand());
        this.addChildren(new ClaimSubCommand());
        this.addChildren(new LeaveSubCommand());
        this.addChildren(new SetLevelSubCommand());
        this.addChildren(new AddLevelSubCommand());
        this.addChildren(new OpenMerchantCommand());
    }

    @Override
    protected boolean noChildrenExecute(CommandSender commandSender, String string, String[] args) {
        return false;
    }

    @Override
    protected void sendUseMessage(CommandSender commandSender, String string, String[] args) {
        if (!commandSender.isOp() || !commandSender.hasPermission("safari.*")) {
            //commandSender.sendMessage(VelocityFeatures.chat(MainConfig.getInstance().commandPrefix + "No permission to execute this command."));
            return;
        }

        commandSender.sendMessage(VelocityFeatures.chat(MainConfig.getInstance().commandPrefix + "/safari give <player>"));
        commandSender.sendMessage(VelocityFeatures.chat(MainConfig.getInstance().commandPrefix + "/safari reload"));
        commandSender.sendMessage(VelocityFeatures.chat(MainConfig.getInstance().commandPrefix + "/safari claim"));
        commandSender.sendMessage(VelocityFeatures.chat(MainConfig.getInstance().commandPrefix + "/safari leave"));
        commandSender.sendMessage(VelocityFeatures.chat(MainConfig.getInstance().commandPrefix + "/safari openmerchant <player>"));
    }

}