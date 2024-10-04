package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.masks.commands;

import org.bukkit.command.CommandSender;
import xyz.velocity.VelocityFeatures;
import xyz.velocity.commands.BaseCommand;
import xyz.velocity.commands.annotations.Command;
import xyz.velocity.MainConfig;

@Command(name = "custommasks")
public class MaskCommand extends BaseCommand {

    public MaskCommand() {
        super(MaskCommand.class.getAnnotation(Command.class).name());

        this.addChildren(new ReloadSubCommand());
        this.addChildren(new GiveSubCommand());
        this.addChildren(new GiveRandomSubCommand());
    }

    @Override
    protected boolean noChildrenExecute(CommandSender commandSender, String string, String[] args) {
        return false;
    }

    @Override
    protected void sendUseMessage(CommandSender commandSender, String string, String[] args) {
        commandSender.sendMessage(VelocityFeatures.chat(MainConfig.getInstance().commandPrefix + "/custommasks give <player> <random/mask name>"));
        commandSender.sendMessage(VelocityFeatures.chat(MainConfig.getInstance().commandPrefix + "/custommasks giverandom <player> <tier>"));
    }

}
