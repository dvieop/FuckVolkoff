package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.pets.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import xyz.velocity.VelocityFeatures;
import xyz.velocity.commands.ChildCommand;
import xyz.velocity.MainConfig;
import xyz.velocity.modules.pets.CustomPet;
import xyz.velocity.modules.pets.Pets;
import xyz.velocity.modules.pets.config.PetsConfig;
import xyz.velocity.modules.pets.config.saves.PetStats;
import xyz.velocity.modules.pets.config.saves.PetTierSave;

import java.util.Arrays;
import java.util.List;

public class GiveSubCommand extends ChildCommand {

    @Override
    protected boolean noChildrenExecute(CommandSender commandSender, String string, String[] args) {
        if (!commandSender.isOp() || !commandSender.hasPermission("pets.*")) {
            commandSender.sendMessage(VelocityFeatures.chat(MainConfig.getInstance().commandPrefix + "No permission to execute this command."));
            return false;
        }

        Player toGive = Bukkit.getPlayer(args[0]);
        String pet = args[1];
        int level = 1;

        try {
            level = Integer.parseInt(args[2]);
        } catch (Throwable err) {

        }

        if (toGive != null && pet != null) {
            CustomPet customPet = Pets.petList.get(pet);

            if (customPet == null) return false;

            ItemStack petItem = customPet.getItem();

            PetTierSave petTierSave = PetsConfig.getInstance().petTiers
                    .stream()
                    .filter(obj -> obj.getTier() == customPet.getPetSave().getTier())
                    .findFirst()
                    .orElse(null);
            PetStats petStats = new PetStats(customPet.getPetSave().getName(), level, 0, (int) Pets.getInstance().calculateXP(petTierSave, level), 1.0);

            Pets.getInstance().updateItemLore(petItem, customPet.getPetSave(), petStats);
            petItem = Pets.getInstance().updateItemNBT(petItem, petStats);

            toGive.getInventory().addItem(petItem);
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
