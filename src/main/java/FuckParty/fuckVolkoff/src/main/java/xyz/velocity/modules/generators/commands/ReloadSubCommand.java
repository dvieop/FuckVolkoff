package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.generators.commands;

import dev.lyons.configapi.ConfigAPI;
import org.bukkit.command.CommandSender;
import xyz.velocity.MainConfig;
import xyz.velocity.VelocityFeatures;
import xyz.velocity.commands.ChildCommand;
import xyz.velocity.modules.generators.GenCache;
import xyz.velocity.modules.generators.Generator;
import xyz.velocity.modules.generators.config.GeneratorConfig;

import java.util.Arrays;
import java.util.List;

public class ReloadSubCommand extends ChildCommand {

    @Override
    protected boolean noChildrenExecute(CommandSender commandSender, String string, String[] args) {
        if (!commandSender.isOp() || !commandSender.hasPermission("generator.*")) {
            commandSender.sendMessage(VelocityFeatures.chat(MainConfig.getInstance().commandPrefix + "No permission to execute this command."));
            return false;
        }

        for (GenCache placedGenerator : Generator.placedGenerators) {
            placedGenerator.getHologram().deleteHologram();
        }

        ConfigAPI.getInstance().loadSpecificConfig(GeneratorConfig.getInstance(), false, false);
        Generator.getInstance().loadGenerators();

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
