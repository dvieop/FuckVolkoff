package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.armorsets.commands;

import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.velocity.VelocityFeatures;
import xyz.velocity.commands.ChildCommand;
import xyz.velocity.MainConfig;
import xyz.velocity.modules.armorsets.config.saves.ItemSave;
import xyz.velocity.modules.armorsets.config.SpecialItemsConfig;
import xyz.velocity.modules.armorsets.ArmorSets;
import xyz.velocity.modules.armorsets.CustomSet;

import java.util.Arrays;
import java.util.List;

public class GiveSubCommand extends ChildCommand {

    @Override
    protected boolean noChildrenExecute(CommandSender commandSender, String string, String[] args) {
        if (!commandSender.isOp() || !commandSender.hasPermission("armorsets.*")) {
            commandSender.sendMessage(VelocityFeatures.chat(MainConfig.getInstance().commandPrefix + "No permission to execute this command."));
            return false;
        }

        if (args.length < 4) {
            this.sendUseMessage(commandSender, string, args);
            return false;
        }

        Player p = Bukkit.getPlayer(args[0]);
        String type = args[1];
        String set = args[2];
        String pieces = args[3];

        if (p == null || pieces == null || set == null || type == null) return false;

        if (type.equals("item")) {
            ItemSave weaponSave = SpecialItemsConfig.getInstance().items.stream().filter(obj -> obj.getName().equals(set)).findFirst().orElse(null);

            if (weaponSave == null) return false;

            createItem(p, weaponSave);
        } else if (type.equals("armor")) {
            CustomSet customSet = ArmorSets.setsCache.get(set);

            if (customSet == null) return false;
            if (pieces.equals("full")) {
                customSet.getItems().forEach((s, itemStack) -> {
                    p.getInventory().addItem(itemStack);
                });
            } else {
                addItem(pieces, customSet, p);
            }
        }

        return false;
    }

    private void addItem(String type, CustomSet customSet, Player p) {
        ItemStack toAdd = null;

        switch (type) {
            case "helmet":
                toAdd = customSet.getHelmet();
                break;
            case "chestplate":
                toAdd = customSet.getChestplate();
                break;
            case "leggings":
                toAdd = customSet.getLeggings();
                break;
            case "boots":
                toAdd = customSet.getBoots();
                break;
        }

        if (toAdd == null) return;

        p.getInventory().addItem(toAdd);
    }

    private void createItem(Player player, ItemSave weaponSave) {

        ItemStack itemStack = new ItemStack(Material.getMaterial(weaponSave.getMaterial()), 1);

        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(VelocityFeatures.chat(weaponSave.getDisplayName()));

        String lore = VelocityFeatures.chat(String.join("VDIB", weaponSave.getLore()));

        itemMeta.setLore(Arrays.asList(lore.split("VDIB")));
        itemStack.setItemMeta(itemMeta);

        addEnchantsToItem(itemStack, weaponSave.getEnchants());

        NBTItem nbtItem = new NBTItem(itemStack);

        NBTCompound nbtCompound = nbtItem.addCompound("velocity_armorsets_item");
        nbtCompound.setString("id", weaponSave.getName());

        if (weaponSave.getNbt().isEnabled()) {
            NBTCompound compound = nbtItem.addCompound(weaponSave.getNbt().getCompound());
            compound.setString(weaponSave.getNbt().getKey(), weaponSave.getNbt().getValue());
        }

        player.getInventory().addItem(nbtItem.getItem());

    }

    private void addEnchantsToItem(ItemStack item, List<String> enchants) {

        for (String enchant : enchants) {

            String[] split = enchant.split(":");

            if (split.length < 2) continue;

            try {
                Enchantment enchantment = Enchantment.getByName(split[0].toUpperCase());
                item.addUnsafeEnchantment(enchantment, Integer.parseInt(split[1]));
            } catch (Throwable e) {
                e.printStackTrace();
            }

        }

    }

    @Override
    public String getName() {
        return "give";
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("give");
    }

    @Override
    protected void sendUseMessage(CommandSender commandSender, String string, String[] args) {
        commandSender.sendMessage(VelocityFeatures.chat(MainConfig.getInstance().commandPrefix + "/armorsets give <player> <item/armor> <set name> <full/helmet/chestplate/leggings/boots>"));
    }

}
