package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.pets.commands;

import dev.lyons.configapi.ConfigAPI;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.velocity.VelocityFeatures;
import xyz.velocity.commands.ChildCommand;
import xyz.velocity.MainConfig;
import xyz.velocity.modules.pets.config.PetsConfig;
import xyz.velocity.modules.pets.config.StatsConfig;
import xyz.velocity.modules.pets.config.saves.StatsSave;
import xyz.velocity.modules.pets.PetWrapper;
import xyz.velocity.modules.pets.Pets;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class ReloadSubCommand extends ChildCommand {

    @Override
    protected boolean noChildrenExecute(CommandSender commandSender, String string, String[] args) {
        if (!commandSender.isOp() || !commandSender.hasPermission("pets.*")) {
            commandSender.sendMessage(VelocityFeatures.chat(MainConfig.getInstance().commandPrefix + "No permission to execute this command."));
            return false;
        }

        ConfigAPI.getInstance().loadSpecificConfig(PetsConfig.getInstance(), false, false);
        commandSender.sendMessage(VelocityFeatures.chat("&8(&9Velocity&8) &fReloaded the configs."));

        Object2ObjectMap<UUID, PetWrapper> petsCache = Pets.equippedPets.clone();

        for (Object2ObjectMap.Entry<UUID, PetWrapper> entry : petsCache.object2ObjectEntrySet()) {
            entry.getValue().deleteHologram();
        }

        Pets.getInstance().loadPets();

        for (Object2ObjectMap.Entry<UUID, PetWrapper> entry : petsCache.object2ObjectEntrySet()) {
            Player p = Bukkit.getPlayer(entry.getKey());

            StatsSave statsSave = StatsConfig.getInstance().getPlayerPets(p);

            if (statsSave.getEquippedPet() == null) continue;

            PetWrapper petWrapper = new PetWrapper(p, Pets.petList.get(statsSave.getEquippedPet().getName()), statsSave.getEquippedPet());

            Pets.equippedPets.put(p.getUniqueId(), petWrapper);
        }

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
