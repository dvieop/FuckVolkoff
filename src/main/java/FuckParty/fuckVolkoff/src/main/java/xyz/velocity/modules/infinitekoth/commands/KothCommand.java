package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.infinitekoth.commands;

import org.bukkit.command.CommandSender;
import xyz.velocity.VelocityFeatures;
import xyz.velocity.commands.BaseCommand;
import xyz.velocity.commands.annotations.Command;
import xyz.velocity.MainConfig;

@Command(name = "infinitekoth")
public class KothCommand extends BaseCommand {

    public KothCommand() {
        super(KothCommand.class.getAnnotation(Command.class).name());

        this.addChildren(new ReloadSubCommand());
    }

    @Override
    public boolean noChildrenExecute(CommandSender commandSender, String string, String[] args) {
        return false;
    }

    @Override
    protected void sendUseMessage(CommandSender commandSender, String string, String[] args) {
        commandSender.sendMessage(VelocityFeatures.chat(MainConfig.getInstance().commandPrefix + "/infinitekoth reload"));
        commandSender.sendMessage(VelocityFeatures.chat(MainConfig.getInstance().commandPrefix + "/infinitekoth start"));
        commandSender.sendMessage(VelocityFeatures.chat(MainConfig.getInstance().commandPrefix + "/infinitekoth stop"));
    }

}
