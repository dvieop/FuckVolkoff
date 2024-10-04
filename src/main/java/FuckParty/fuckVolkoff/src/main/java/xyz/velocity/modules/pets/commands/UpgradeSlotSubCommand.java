package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.pets.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.velocity.VelocityFeatures;
import xyz.velocity.commands.ChildCommand;
import xyz.velocity.MainConfig;
import xyz.velocity.modules.pets.config.StatsConfig;
import xyz.velocity.modules.pets.config.saves.StatsSave;

import java.util.Arrays;
import java.util.List;

public class UpgradeSlotSubCommand extends ChildCommand {

    @Override
    protected boolean noChildrenExecute(CommandSender commandSender, String string, String[] args) {
        if (!commandSender.isOp() || !commandSender.hasPermission("pets.*")) {
            commandSender.sendMessage(VelocityFeatures.chat(MainConfig.getInstance().commandPrefix + "No permission to execute this command."));
            return false;
        }

        Player toGive = Bukkit.getPlayer(args[0]);

        if (toGive != null) {
            StatsSave statsSave = StatsConfig.getInstance().getPlayerPets(toGive);

            statsSave.setSlots(statsSave.getSlots() + 1);

            StatsConfig.getInstance().getData().put(toGive.getUniqueId(), statsSave);
            //StatsConfig.getInstance().saveData();
        }

        return false;
    }

    @Override
    public String getName() {
        return "upgradeslot";
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("upgradeslot");
    }

}
