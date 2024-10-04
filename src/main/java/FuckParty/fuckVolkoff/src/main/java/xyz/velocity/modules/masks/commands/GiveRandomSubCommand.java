package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.masks.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.velocity.MainConfig;
import xyz.velocity.VelocityFeatures;
import xyz.velocity.commands.ChildCommand;
import xyz.velocity.modules.masks.CustomMask;
import xyz.velocity.modules.masks.Masks;
import xyz.velocity.modules.masks.config.MaskConfig;
import xyz.velocity.modules.masks.config.MaskSave;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class GiveRandomSubCommand extends ChildCommand {

    @Override
    protected boolean noChildrenExecute(CommandSender commandSender, String string, String[] args) {
        if (!commandSender.isOp() || !commandSender.hasPermission("masks.*")) {
            commandSender.sendMessage(VelocityFeatures.chat(MainConfig.getInstance().commandPrefix + "No permission to execute this command."));
            return false;
        }

        Player toGive = Bukkit.getPlayer(args[0]);
        int tier;

        if (args.length > 1 && args[1] != null) tier = Integer.parseInt(args[1]);
        else {
            tier = 0;
        }
        if (toGive != null) {
            List<MaskSave> maskSaves;

            if (tier == 0) maskSaves = MaskConfig.getInstance().masks;
            else maskSaves = MaskConfig.getInstance().masks.stream().filter(obj -> obj.getTier() == tier).collect(Collectors.toList());

            if (maskSaves.isEmpty()) {
                return false;
            }

            double totalChances = 0.0;

            for (MaskSave maskSave : maskSaves) {
                totalChances += maskSave.getChanceToObtain();
            }

            int index = 0;

            for (double r = Math.random() * totalChances; index < maskSaves.size() - 1; ++index) {
                r -= maskSaves.get(index).getChanceToObtain();
                if (r <= 0.0) break;
            }

            MaskSave maskSave = maskSaves.get(index);
            CustomMask customMask = Masks.maskList.get(maskSave.getName());

            if (customMask == null) return false;

            toGive.getInventory().addItem(customMask.getItem());
        }

        return false;
    }

    @Override
    public String getName() {
        return "giverandom";
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("giverandom");
    }

}
