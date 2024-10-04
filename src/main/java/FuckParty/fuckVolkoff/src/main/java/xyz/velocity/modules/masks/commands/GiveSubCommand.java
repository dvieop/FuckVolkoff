package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.masks.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.velocity.VelocityFeatures;
import xyz.velocity.commands.ChildCommand;
import xyz.velocity.MainConfig;
import xyz.velocity.modules.masks.config.MaskConfig;
import xyz.velocity.modules.masks.config.MaskSave;
import xyz.velocity.modules.masks.CustomMask;
import xyz.velocity.modules.masks.Masks;

import java.util.Arrays;
import java.util.List;

public class GiveSubCommand extends ChildCommand {

    @Override
    protected boolean noChildrenExecute(CommandSender commandSender, String string, String[] args) {
        if (!commandSender.isOp() || !commandSender.hasPermission("masks.*")) {
            commandSender.sendMessage(VelocityFeatures.chat(MainConfig.getInstance().commandPrefix + "No permission to execute this command."));
            return false;
        }

        Player toGive = Bukkit.getPlayer(args[0]);
        String mask = args[1];

        if (toGive != null && mask != null) {
            CustomMask customMask = Masks.maskList.get(mask);

            if (customMask == null) return false;

            toGive.getInventory().addItem(customMask.getItem());
        }

        return false;
    }

    @Override
    public String getName() {
        return "give";
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("give");
    }

}
