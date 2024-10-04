package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.mining.commands;

import dev.lyons.configapi.ConfigAPI;
import org.bukkit.command.CommandSender;
import xyz.velocity.MainConfig;
import xyz.velocity.VelocityFeatures;
import xyz.velocity.commands.ChildCommand;
import xyz.velocity.modules.mining.Mining;
import xyz.velocity.modules.mining.config.MiningConfig;

import java.util.ArrayList;
import java.util.List;

public class ReloadSubCommand extends ChildCommand {

    @Override
    protected boolean noChildrenExecute(CommandSender commandSender, String string, String[] args) {
        if (!commandSender.isOp() || !commandSender.hasPermission("mining.*")) {
           commandSender.sendMessage(VelocityFeatures.chat(MainConfig.getInstance().commandPrefix + "No permission to execute this command."));
           return false;
        }

        ConfigAPI.getInstance().loadSpecificConfig(MiningConfig.getInstance(), false, false);
        Mining.getInstance().resetBlocks();
        Mining.getInstance().loadBlocks();

        commandSender.sendMessage(VelocityFeatures.chat("&8(&9Velocity&8) &fReloaded the configs."));
        return false;
    }

    @Override
    public String getName() {
        return "reload";
    }

    @Override
    public List<String> getAliases() {
        List<String> aliases = new ArrayList<>();
        aliases.add("reload");
        return aliases;
    }

}
