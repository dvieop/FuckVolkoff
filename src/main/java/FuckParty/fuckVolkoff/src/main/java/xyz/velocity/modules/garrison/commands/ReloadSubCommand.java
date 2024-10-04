package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.garrison.commands;

import dev.lyons.configapi.ConfigAPI;
import org.bukkit.command.CommandSender;
import xyz.velocity.MainConfig;
import xyz.velocity.VelocityFeatures;
import xyz.velocity.commands.ChildCommand;
import xyz.velocity.modules.garrison.Garrison;
import xyz.velocity.modules.garrison.Hologram;
import xyz.velocity.modules.garrison.config.GarrisonConfig;

import java.util.Arrays;
import java.util.List;

public class ReloadSubCommand extends ChildCommand {

    @Override
    protected boolean noChildrenExecute(CommandSender commandSender, String string, String[] args) {
        if (!commandSender.isOp() || !commandSender.hasPermission("garrison.*")) {
            commandSender.sendMessage(VelocityFeatures.chat(MainConfig.getInstance().commandPrefix + "No permission to execute this command."));
            return false;
        }

        //Garrison.getInstance().updateData(Garrison.getInstance().capturePoint);
        ConfigAPI.getInstance().loadSpecificConfig(GarrisonConfig.getInstance(), false, false);

        Garrison.getInstance().hologram.deleteHologram();
        Garrison.getInstance().hologram = new Hologram(xyz.velocity.modules.util.Location.parseToLocation(GarrisonConfig.getInstance().hologram.getLocation()));

        Garrison.getInstance().loadData();

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
