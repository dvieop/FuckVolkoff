package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.killtracker.commands;

import org.bukkit.command.CommandSender;
import xyz.velocity.VelocityFeatures;
import xyz.velocity.commands.BaseCommand;
import xyz.velocity.commands.annotations.Command;
import xyz.velocity.MainConfig;

@Command(name = "killtracker")
public class KilltrackerCommand extends BaseCommand {

    public KilltrackerCommand() {
        super(KilltrackerCommand.class.getAnnotation(Command.class).name());

        this.addChildren(new ReloadSubCommand());
    }

    @Override
    protected boolean noChildrenExecute(CommandSender commandSender, String string, String[] args) {
        return false;
    }

    @Override
    protected void sendUseMessage(CommandSender commandSender, String string, String[] args) {
        if (!commandSender.isOp() || !commandSender.hasPermission("killtracker.*")) {
            commandSender.sendMessage(VelocityFeatures.chat(MainConfig.getInstance().commandPrefix + "No permission to execute this command."));
            return;
        }

        commandSender.sendMessage(VelocityFeatures.chat(MainConfig.getInstance().commandPrefix + "/killtracker reload"));
    }

}