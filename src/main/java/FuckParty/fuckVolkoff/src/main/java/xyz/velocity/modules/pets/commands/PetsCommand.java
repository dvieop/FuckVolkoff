package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.pets.commands;

import org.bukkit.command.CommandSender;
import xyz.velocity.VelocityFeatures;
import xyz.velocity.commands.BaseCommand;
import xyz.velocity.commands.annotations.Command;
import xyz.velocity.MainConfig;

@Command(name = "pets")
public class PetsCommand extends BaseCommand {

    public PetsCommand() {
        super(PetsCommand.class.getAnnotation(Command.class).name());

        this.addChildren(new GiveSubCommand());
        this.addChildren(new GiveRandomSubCommand());
        this.addChildren(new ReloadSubCommand());
        this.addChildren(new InventorySubCommand());
        this.addChildren(new GiveShardSubCommand());
        this.addChildren(new UpgradeSlotSubCommand());
    }

    @Override
    protected boolean noChildrenExecute(CommandSender commandSender, String string, String[] args) {
        return false;
    }

    @Override
    protected void sendUseMessage(CommandSender commandSender, String string, String[] args) {
        if (!commandSender.isOp() || !commandSender.hasPermission("pets.*")) {
            commandSender.sendMessage(VelocityFeatures.chat(MainConfig.getInstance().commandPrefix + "/pets inventory"));
            return;
        }

        commandSender.sendMessage(VelocityFeatures.chat(MainConfig.getInstance().commandPrefix + "/pets inventory"));
        commandSender.sendMessage(VelocityFeatures.chat(MainConfig.getInstance().commandPrefix + "/pets give <player> <name>"));
        commandSender.sendMessage(VelocityFeatures.chat(MainConfig.getInstance().commandPrefix + "/pets giverandom <player> <tier> [level]"));
        commandSender.sendMessage(VelocityFeatures.chat(MainConfig.getInstance().commandPrefix + "/pets giveshard <player> <multiplier>"));
        commandSender.sendMessage(VelocityFeatures.chat(MainConfig.getInstance().commandPrefix + "/pets upgradeslot <player>"));
    }

}
