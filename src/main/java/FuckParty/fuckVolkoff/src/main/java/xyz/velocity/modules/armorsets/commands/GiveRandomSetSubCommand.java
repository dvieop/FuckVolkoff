package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.armorsets.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import xyz.velocity.VelocityFeatures;
import xyz.velocity.commands.ChildCommand;
import xyz.velocity.MainConfig;
import xyz.velocity.modules.armorsets.config.ArmorConfig;
import xyz.velocity.modules.armorsets.config.saves.ArmorSave;
import xyz.velocity.modules.armorsets.ArmorSets;
import xyz.velocity.modules.armorsets.CustomSet;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class GiveRandomSetSubCommand extends ChildCommand {

    @Override
    protected boolean noChildrenExecute(CommandSender commandSender, String string, String[] args) {
        if (!commandSender.isOp() || !commandSender.hasPermission("armorsets.*")) {
            commandSender.sendMessage(VelocityFeatures.chat(MainConfig.getInstance().commandPrefix + "No permission to execute this command."));
            return false;
        }

        if (args.length < 3) {
            this.sendUseMessage(commandSender, string, args);
            return false;
        }

        Player p = Bukkit.getPlayer(args[0]);
        String set = args[1];
        String type = args[2];

        if (p == null || set == null || type == null) return false;

        if (set.equals("random")) {
            List<ArmorSave> armorSaves = ArmorConfig.getInstance().sets;

            if (armorSaves.size() < 1) {
                return false;
            }

            double totalChances = 0.0;

            for (ArmorSave armorSave : armorSaves) {
                totalChances += armorSave.getChanceToObtain();
            }

            int index = 0;

            for (double r = Math.random() * totalChances; index < armorSaves.size() - 1; ++index) {
                r -= armorSaves.get(index).getChanceToObtain();
                if (r <= 0.0) break;
            }

            ArmorSave armorSave = armorSaves.get(index);
            CustomSet customSet = ArmorSets.setsCache.get(armorSave.getName());

            if (customSet == null) return false;

            if (armorSave != null) {
                if (type.equals("full")) {
                    customSet.getItems().forEach((s, itemStack) -> {
                        p.getInventory().addItem(itemStack);
                    });
                } else {
                    giveRandomPiece(p, customSet);
                }
            }
        } else {
            CustomSet customSet = ArmorSets.setsCache.get(set);

            if (customSet == null) return false;

            if (type.equals("full")) {
                customSet.getItems().forEach((s, itemStack) -> {
                    p.getInventory().addItem(itemStack);
                });
            } else {
                giveRandomPiece(p, customSet);
            }
        }

        return false;
    }

    private void giveRandomPiece(Player player, CustomSet customSet) {
        List<String> pieces = customSet.getItems().keySet().stream().collect(Collectors.toList());

        if (pieces.size() < 1) {
            return;
        }

        double pieceChances = 0.0;

        for (String s : pieces) {
            pieceChances += 25;
        }

        int armorIndex = 0;

        for (double r = Math.random() * pieceChances; armorIndex < pieces.size() - 1; ++armorIndex) {
            r -= 25;
            if (r <= 0.0) break;
        }

        String toGive = pieces.get(armorIndex);

        if (toGive == null) return;

        ItemStack itemStack = customSet.getItems().get(toGive);

        player.getInventory().addItem(itemStack);
    }

    @Override
    public String getName() {
        return "giverandomset";
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("giverandomset");
    }

    @Override
    protected void sendUseMessage(CommandSender commandSender, String string, String[] args) {
        commandSender.sendMessage(VelocityFeatures.chat(MainConfig.getInstance().commandPrefix + "/armorsets giverandomset <player> <random/set name> <full/piece>"));
    }

}
