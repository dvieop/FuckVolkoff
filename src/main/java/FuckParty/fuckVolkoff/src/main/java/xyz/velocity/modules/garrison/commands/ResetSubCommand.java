package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.garrison.commands;

import com.golfing8.kore.FactionsKore;
import org.bukkit.command.CommandSender;
import xyz.velocity.MainConfig;
import xyz.velocity.VelocityFeatures;
import xyz.velocity.commands.ChildCommand;
import xyz.velocity.modules.garrison.Garrison;
import xyz.velocity.modules.garrison.config.DataConfig;
import xyz.velocity.modules.garrison.config.saves.DataSave;
import xyz.velocity.modules.util.CapturePoint;

import java.util.Arrays;
import java.util.List;

public class ResetSubCommand extends ChildCommand {

    @Override
    protected boolean noChildrenExecute(CommandSender commandSender, String string, String[] args) {
        if (!commandSender.isOp() || !commandSender.hasPermission("garrison.*")) {
            commandSender.sendMessage(VelocityFeatures.chat(MainConfig.getInstance().commandPrefix + "No permission to execute this command."));
            return false;
        }

        Garrison.getInstance().resetGarrison(Garrison.getInstance().capturePoint);

        commandSender.sendMessage(VelocityFeatures.chat(MainConfig.getInstance().commandPrefix + "Successfully force set the stronghold"));

        return false;
    }

    @Override
    public String getName() {
        return "reset";
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("reset");
    }

}
