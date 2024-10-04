package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.mining.commands;

import org.bukkit.command.CommandSender;
import xyz.velocity.VelocityFeatures;
import xyz.velocity.commands.BaseCommand;
import xyz.velocity.commands.annotations.Command;
import xyz.velocity.MainConfig;

@Command(name = "mining")
public class MiningCommand extends BaseCommand {

    public MiningCommand() {
        super(MiningCommand.class.getAnnotation(Command.class).name());

        this.addChildren(new ReloadSubCommand());
        this.addChildren(new OpenMerchantCommand());
    }

    @Override
    protected boolean noChildrenExecute(CommandSender commandSender, String string, String[] args) {
        return false;
    }

    @Override
    protected void sendUseMessage(CommandSender commandSender, String string, String[] args) {
        if (!commandSender.isOp() || !commandSender.hasPermission("mining.*")) return;

        commandSender.sendMessage(VelocityFeatures.chat(MainConfig.getInstance().commandPrefix + "/mining reload"));
        commandSender.sendMessage(VelocityFeatures.chat(MainConfig.getInstance().commandPrefix + "/mining openmerchant <player>"));
    }

}
