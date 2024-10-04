package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.generators.commands;

import org.bukkit.command.CommandSender;
import xyz.velocity.MainConfig;
import xyz.velocity.VelocityFeatures;
import xyz.velocity.commands.BaseCommand;
import xyz.velocity.commands.annotations.Command;

@Command(name = "generator")
public class GeneratorCommand extends BaseCommand {
    public GeneratorCommand() {
        super(GeneratorCommand.class.getAnnotation(Command.class).name());

        this.addChildren(new ReloadSubCommand());
        this.addChildren(new GiveSubCommand());
        this.addChildren(new ListSubCommand());
    }

    @Override
    protected boolean noChildrenExecute(CommandSender commandSender, String string, String[] args) {
        return false;
    }

    @Override
    protected void sendUseMessage(CommandSender commandSender, String string, String[] args) {
        if (!commandSender.isOp() || !commandSender.hasPermission("generator.*")) {
            commandSender.sendMessage(VelocityFeatures.chat(MainConfig.getInstance().commandPrefix + "/generator list"));
            return;
        }

        commandSender.sendMessage(VelocityFeatures.chat(MainConfig.getInstance().commandPrefix + "/generator reload"));
        commandSender.sendMessage(VelocityFeatures.chat(MainConfig.getInstance().commandPrefix + "/generator give <player> <type> [tier]"));
    }

}
