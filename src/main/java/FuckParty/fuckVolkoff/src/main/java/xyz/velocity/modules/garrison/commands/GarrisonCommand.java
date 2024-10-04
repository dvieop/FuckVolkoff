package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.garrison.commands;

import org.bukkit.command.CommandSender;
import xyz.velocity.MainConfig;
import xyz.velocity.VelocityFeatures;
import xyz.velocity.commands.BaseCommand;
import xyz.velocity.commands.annotations.Command;

@Command(name = "garrison")
public class GarrisonCommand extends BaseCommand {
    public GarrisonCommand() {
        super(GarrisonCommand.class.getAnnotation(Command.class).name());

        this.addChildren(new ReloadSubCommand());
        this.addChildren(new ResetSubCommand());
        this.addChildren(new SetOwnerSubCommand());
    }

    @Override
    protected boolean noChildrenExecute(CommandSender commandSender, String string, String[] args) {
        return false;
    }

    @Override
    protected void sendUseMessage(CommandSender commandSender, String string, String[] args) {
        if (!commandSender.isOp() || !commandSender.hasPermission("garrison.*")) {
            commandSender.sendMessage(VelocityFeatures.chat(MainConfig.getInstance().commandPrefix + "No permission to execute this command."));
            return;
        }

        commandSender.sendMessage(VelocityFeatures.chat(MainConfig.getInstance().commandPrefix + "/garrison reload"));
        commandSender.sendMessage(VelocityFeatures.chat(MainConfig.getInstance().commandPrefix + "/garrison reset"));
        commandSender.sendMessage(VelocityFeatures.chat(MainConfig.getInstance().commandPrefix + "/garrison setowner <factionTag>"));
    }

}
