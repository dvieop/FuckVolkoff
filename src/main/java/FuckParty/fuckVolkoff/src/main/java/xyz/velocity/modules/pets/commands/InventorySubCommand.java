package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.pets.commands;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.velocity.VelocityFeatures;
import xyz.velocity.commands.ChildCommand;
import xyz.velocity.modules.pets.config.PetsConfig;
import xyz.velocity.modules.pets.config.StatsConfig;
import xyz.velocity.modules.pets.config.saves.*;
import xyz.velocity.modules.pets.CustomPet;
import xyz.velocity.modules.pets.Pets;
import xyz.velocity.modules.util.ItemUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InventorySubCommand extends ChildCommand {

    @Override
    protected boolean noChildrenExecute(CommandSender commandSender, String string, String[] args) {
        openInventory((Player) commandSender);
        return false;
    }

    private static void openInventory(Player player) {
        PetsConfig config = PetsConfig.getInstance();

        Inventory inv = Bukkit.createInventory(null, config.inventory.getSize(), VelocityFeatures.chat(config.inventory.getGuiName()));
        addInvContents(player, inv, config);

        player.openInventory(inv);
    }

    public static void reloadInventory(Player player, Inventory inventory) {
        inventory.clear();

        addInvContents(player, inventory, PetsConfig.getInstance());
    }

    private static void addInvContents(Player player, Inventory inv, PetsConfig config) {

        StatsSave statsSave = StatsConfig.getInstance().getPlayerPets(player);

        for (InventoryItemSave inventoryItemSave : config.inventory.getFiller()) {

            ItemStack itemStack = new ItemStack(Material.getMaterial(inventoryItemSave.getMaterial()), 1, (byte) inventoryItemSave.getData());
            ItemMeta meta = itemStack.getItemMeta();

            meta.setDisplayName(VelocityFeatures.chat("&7 "));
            itemStack.setItemMeta(meta);

            inv.setItem(inventoryItemSave.getSlot(), itemStack);

        }

        //setPetEquippedSlot(inv, statsSave, config);
        setSpecialSlot(player, inv, config.inventory.getToggleItem(), config.getInventory().getToggleItem().getSlot());
        setSpecialSlot(player, inv, config.inventory.getUnEquipItem(), config.getInventory().getUnEquipItem().getSlot());
        setSpecialSlot(player, inv, config.inventory.getTakeOutItem(), config.getInventory().getTakeOutItem().getSlot());

        int iPet = 0;

        for (int i = 0; i < inv.getSize(); i++) {
            if (!ItemUtil.isAirOrNull(inv.getItem(i))) continue;

            PetStats petStats;

            try {
                petStats = statsSave.getPetInventory().get(iPet);
            } catch (Throwable e) {
                if (iPet + 1 > statsSave.getSlots()) {
                    setSpecialSlot(player, inv, config.getInventory().getLockedItem(), i);
                }
                iPet += 1;
                continue;
            }

            iPet += 1;

            CustomPet customPet = Pets.petList.get(petStats.getName());

            if (customPet == null) continue;

            ItemStack petItem = petItem(petStats);

            if (statsSave.getEquippedPet() != null && statsSave.getEquippedPet().getName().equals(petStats.getName())) {
                ItemMeta meta = petItem.getItemMeta();

                meta.setDisplayName(VelocityFeatures.chat(meta.getDisplayName() + " &8[&a&lEQUIPPED&8]"));

                petItem.setItemMeta(meta);
            }

            inv.setItem(i, petItem);
        }

    }

    private static ItemStack petItem(PetStats petStats) {
        CustomPet customPet = Pets.petList.get(petStats.getName());
        PetSave petSave = customPet.getPetSave();

        ItemStack petItem = Pets.petList.get(petStats.getName()).getItem();

        String statsLore = Pets.getInstance().getStatsLore(petSave, petStats);

        List<String> originalLore = new ArrayList<>();

        originalLore.addAll(petSave.getInventoryLore());

        if (petStats.getXpBoost() > 1.0) {
            AttachableItemSave ais = PetsConfig.getInstance().attachableItems.stream()
                    .filter(obj -> obj.getName().equals("xpshard"))
                    .findFirst()
                    .orElse(null);

            if (ais != null) {
                originalLore.add("&7 ");
                originalLore.add(ais.getAttachedLore().replace("<multiplier>", petStats.getXpBoost() + ""));
            }
        }

        String lore = String.join("VDIB", originalLore);

        lore = VelocityFeatures.chat(lore
                .replace("<xp>", petStats.getXp() + "")
                .replace("<level>", petStats.getLevel() + "")
                .replace("<xpToLevelUp>", petStats.getXpToLevelUp() + "")
                .replace("<petStats>", statsLore)
        );

        ItemMeta meta = petItem.getItemMeta();

        meta.setDisplayName(VelocityFeatures.chat(petSave.getDisplayName()
                .replace("<level>", petStats.getLevel() + "")
        ));
        meta.setLore(Arrays.asList(lore.split("VDIB")));

        petItem.setItemMeta(meta);

        return petItem;
    }

    /*private ItemStack barrierItem() {
        ItemStack barrier = new ItemStack(Material.BARRIER);

        ItemMeta meta = barrier.getItemMeta();

        meta.setDisplayName(VelocityFeatures.chat("&c&lNo Pet Equipped"));

        barrier.setItemMeta(meta);

        return barrier;
    }*/

    /*private void setPetEquippedSlot(Inventory inv, StatsSave statsSave, PetsConfig config) {
        PetStats equippedPet = statsSave.getEquippedPet();
        int slot = config.inventory.getEquippedPetSlot();

        if (equippedPet == null) {
            inv.setItem(slot, barrierItem());
        } else {
            ItemStack petItem = petItem(equippedPet);

            inv.setItem(slot, petItem);
        }
    }*/

    private static void setSpecialSlot(Player player, Inventory inv, InventoryItemSave iis, int slot) {
        ItemStack item = new ItemStack(Material.getMaterial(iis.getMaterial()), 1, (byte) iis.getData());

        ItemMeta meta = item.getItemMeta();

        String toggle = Pets.getInstance().isPetToggled(player) ? "&aEnabled" : "&cDisabled";

        meta.setDisplayName(VelocityFeatures.chat(iis.getDisplayName()
                .replace("<status>", toggle)
        ));

        String lore = VelocityFeatures.chat(String.join("VDIB", iis.getLore()));

        meta.setLore(Arrays.asList(lore.split("VDIB")));

        item.setItemMeta(meta);

        inv.setItem(slot, item);
    }

    @Override
    public String getName() {
        return "inventory";
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("inventory");
    }

}
