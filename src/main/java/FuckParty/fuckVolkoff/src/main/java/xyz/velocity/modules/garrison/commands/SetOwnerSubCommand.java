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

public class SetOwnerSubCommand extends ChildCommand {

    @Override
    protected boolean noChildrenExecute(CommandSender commandSender, String string, String[] args) {
        if (!commandSender.isOp() || !commandSender.hasPermission("garrison.*")) {
            commandSender.sendMessage(VelocityFeatures.chat(MainConfig.getInstance().commandPrefix + "No permission to execute this command."));
            return false;
        }

        if (args.length < 1) {
            this.sendUseMessage(commandSender, string, args);
            return false;
        }

        String factionTag = args[0];
        String factionId = FactionsKore.getIntegration().getIdFromTag(factionTag);

        if (factionId == null) {
            commandSender.sendMessage(VelocityFeatures.chat(MainConfig.getInstance().commandPrefix + "Found no faction with that tag"));
            return false;
        }

        CapturePoint capturePoint = Garrison.getInstance().capturePoint;
        DataSave dataSave = DataConfig.getInstance().getGarrison().get(capturePoint.getGarrison().getName());

        capturePoint.setFactionOwning(factionId);
        dataSave.setFaction(factionId);

        commandSender.sendMessage(VelocityFeatures.chat(MainConfig.getInstance().commandPrefix + "Successfully force set the stronghold"));

        return false;
    }

    @Override
    public String getName() {
        return "setowner";
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("setowner");
    }

    @Override
    protected void sendUseMessage(CommandSender commandSender, String string, String[] args) {
        commandSender.sendMessage(VelocityFeatures.chat(MainConfig.getInstance().commandPrefix + "/garrison setowner <factionTag>"));
    }

}
