package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.tntpouch.commands;

import dev.lyons.configapi.ConfigAPI;
import org.bukkit.command.CommandSender;
import xyz.velocity.VelocityFeatures;
import xyz.velocity.commands.ChildCommand;
import xyz.velocity.MainConfig;
import xyz.velocity.modules.tntpouch.config.PouchConfig;

import java.util.Arrays;
import java.util.List;

public class ReloadSubCommand extends ChildCommand {
    @Override
    protected boolean noChildrenExecute(CommandSender commandSender, String string, String[] args) {
        if (!commandSender.isOp() || !commandSender.hasPermission("tntpouch.*")) {
            commandSender.sendMessage(VelocityFeatures.chat(MainConfig.getInstance().commandPrefix + "No permission to execute this command."));
            return false;
        }

        ConfigAPI.getInstance().loadSpecificConfig(PouchConfig.getInstance(), false, false);
        commandSender.sendMessage(VelocityFeatures.chat("&8(&9Velocity&8) &fReloaded the configs."));
        return false;
    }

    @Override
    public String getName() {
        return "reload";
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("reload");
    }
}
