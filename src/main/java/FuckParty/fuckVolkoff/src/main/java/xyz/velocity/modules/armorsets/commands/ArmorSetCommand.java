package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.armorsets.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.velocity.VelocityFeatures;
import xyz.velocity.commands.BaseCommand;
import xyz.velocity.commands.annotations.Command;
import xyz.velocity.MainConfig;

@Command(name = "armorsets")
public class ArmorSetCommand extends BaseCommand {

    public ArmorSetCommand() {
        super(ArmorSetCommand.class.getAnnotation(Command.class).name());

        this.addChildren(new ReloadSubCommand());
        this.addChildren(new GiveSubCommand());
        this.addChildren(new SetHpSubCommand());
        this.addChildren(new GiveRandomSetSubCommand());
    }

    @Override
    protected boolean noChildrenExecute(CommandSender commandSender, String string, String[] args) {
        return false;
    }

    @Override
    protected void sendUseMessage(CommandSender sender, String string, String[] args) {
        if (!sender.isOp() || !sender.hasPermission("armorsets.*")) {
            sender.sendMessage(VelocityFeatures.chat(MainConfig.getInstance().commandPrefix + "No permission to execute this command."));
            return;
        }

        if (!(sender instanceof Player)) return;

        sender.sendMessage(VelocityFeatures.chat(MainConfig.getInstance().commandPrefix + "/armorsets reload"));
        sender.sendMessage(VelocityFeatures.chat(MainConfig.getInstance().commandPrefix + "/armorsets give"));
        sender.sendMessage(VelocityFeatures.chat(MainConfig.getInstance().commandPrefix + "/armorsets sethp <player> <hp>"));
    }

}
