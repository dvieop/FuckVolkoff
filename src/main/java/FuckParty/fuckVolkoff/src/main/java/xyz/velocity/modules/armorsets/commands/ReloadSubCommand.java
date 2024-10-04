package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.armorsets.commands;

import dev.lyons.configapi.ConfigAPI;
import org.bukkit.command.CommandSender;
import xyz.velocity.VelocityFeatures;
import xyz.velocity.commands.ChildCommand;
import xyz.velocity.MainConfig;
import xyz.velocity.modules.armorsets.AbilityManager;
import xyz.velocity.modules.armorsets.config.AbilitiesConfig;
import xyz.velocity.modules.armorsets.config.ArmorConfig;
import xyz.velocity.modules.armorsets.config.SpecialItemsConfig;
import xyz.velocity.modules.armorsets.ArmorSets;

import java.util.ArrayList;
import java.util.List;

public class ReloadSubCommand extends ChildCommand {

    @Override
    protected boolean noChildrenExecute(CommandSender commandSender, String string, String[] args) {
        if (!commandSender.isOp() || !commandSender.hasPermission("armorsets.*")) {
            commandSender.sendMessage(VelocityFeatures.chat(MainConfig.getInstance().commandPrefix + "No permission to execute this command."));
            return false;
        }

        ConfigAPI.getInstance().loadSpecificConfig(ArmorConfig.getInstance(), false, false);
        ConfigAPI.getInstance().loadSpecificConfig(SpecialItemsConfig.getInstance(), false, false);
        ConfigAPI.getInstance().loadSpecificConfig(AbilitiesConfig.getInstance(), false, false);
        ArmorSets.getInstance().loadArmorSets();
        new AbilityManager();

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
