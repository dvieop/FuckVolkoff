package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.mobarena.commands;

import org.bukkit.command.CommandSender;
import xyz.velocity.MainConfig;
import xyz.velocity.VelocityFeatures;
import xyz.velocity.commands.BaseCommand;
import xyz.velocity.commands.annotations.Command;
import xyz.velocity.modules.masks.commands.GiveSubCommand;
import xyz.velocity.modules.masks.commands.MaskCommand;
import xyz.velocity.modules.masks.commands.ReloadSubCommand;

@Command(name = "mobarena")
public class MobArenaCommand extends BaseCommand {

    public MobArenaCommand() {
        super(MobArenaCommand.class.getAnnotation(Command.class).name());

        this.addChildren(new JoinArenaSubCommand());
    }

    @Override
    protected boolean noChildrenExecute(CommandSender commandSender, String string, String[] args) {
        return false;
    }

    @Override
    protected void sendUseMessage(CommandSender commandSender, String string, String[] args) {
        commandSender.sendMessage(VelocityFeatures.chat(MainConfig.getInstance().commandPrefix + "/mobarena join"));
    }

}
