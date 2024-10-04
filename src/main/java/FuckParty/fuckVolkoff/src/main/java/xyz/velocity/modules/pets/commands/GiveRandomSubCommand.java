package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.pets.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import xyz.velocity.VelocityFeatures;
import xyz.velocity.commands.ChildCommand;
import xyz.velocity.MainConfig;
import xyz.velocity.modules.pets.config.PetsConfig;
import xyz.velocity.modules.pets.config.saves.PetStats;
import xyz.velocity.modules.pets.config.saves.PetTierSave;
import xyz.velocity.modules.pets.CustomPet;
import xyz.velocity.modules.pets.Pets;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class GiveRandomSubCommand extends ChildCommand {

    @Override
    protected boolean noChildrenExecute(CommandSender commandSender, String string, String[] args) {
        if (!commandSender.isOp() || !commandSender.hasPermission("pets.*")) {
            commandSender.sendMessage(VelocityFeatures.chat(MainConfig.getInstance().commandPrefix + "No permission to execute this command."));
            return false;
        }

        Player toGive = Bukkit.getPlayer(args[0]);
        int tier = Integer.parseInt(args[1]);
        int level = 1;

        if (args.length > 2 && args[2] != null) {
            level = Integer.parseInt(args[2]);
        }

        if (toGive != null) {
            List<CustomPet> pets = Pets.petList.values().stream().filter(obj -> tier == obj.getPetSave().getTier()).collect(Collectors.toList());

            if (pets.size() < 1) {
                return false;
            }

            double pieceChances = 0.0;

            for (CustomPet s : pets) {
                pieceChances += s.getPetSave().getChance();
            }

            int petIndex = 0;

            for (double r = Math.random() * pieceChances; petIndex < pets.size() - 1; ++petIndex) {
                r -= pets.get(petIndex).getPetSave().getChance();
                if (r <= 0.0) break;
            }

            CustomPet pet = pets.get(petIndex);

            if (pet == null) return false;

            ItemStack petItem = pet.getItem();

            if (level > 1) {
                PetTierSave petTierSave = PetsConfig.getInstance().petTiers.stream().filter(obj -> obj.getTier() == tier).findFirst().orElse(null);
                PetStats petStats = new PetStats(pet.getPetSave().getName(), level, 0, (int) Pets.getInstance().calculateXP(petTierSave, level), 1.0);

                Pets.getInstance().updateItemLore(petItem, pet.getPetSave(), petStats);
                petItem = Pets.getInstance().updateItemNBT(petItem, petStats);
            }

            toGive.getInventory().addItem(petItem);
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
