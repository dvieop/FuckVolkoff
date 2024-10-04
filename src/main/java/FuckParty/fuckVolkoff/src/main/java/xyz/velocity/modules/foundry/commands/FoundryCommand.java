package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.foundry.commands;

import org.bukkit.command.CommandSender;
import xyz.velocity.VelocityFeatures;
import xyz.velocity.commands.BaseCommand;
import xyz.velocity.commands.annotations.Command;
import xyz.velocity.MainConfig;

@Command(name = "foundry")
public class FoundryCommand extends BaseCommand {

    public FoundryCommand() {
        super(FoundryCommand.class.getAnnotation(Command.class).name());

        this.addChildren(new CreditsSubCommand());
        this.addChildren(new ReloadSubCommand());
    }

    @Override
    public boolean noChildrenExecute(CommandSender commandSender, String string, String[] args) {
        return false;
    }

    @Override
    protected void sendUseMessage(CommandSender commandSender, String string, String[] args) {
        commandSender.sendMessage(VelocityFeatures.chat(MainConfig.getInstance().commandPrefix + "/foundry credits give <player> <amount>"));
        commandSender.sendMessage(VelocityFeatures.chat(MainConfig.getInstance().commandPrefix + "/foundry credits set <player> <amount>"));
        commandSender.sendMessage(VelocityFeatures.chat(MainConfig.getInstance().commandPrefix + "/foundry credits bal [player]"));
        commandSender.sendMessage(VelocityFeatures.chat(MainConfig.getInstance().commandPrefix + "/foundry reload"));
    }

}
