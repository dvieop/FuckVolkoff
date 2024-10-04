package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.customenchants.listeners;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.velocity.VelocityFeatures;
import xyz.velocity.bukkitutils.listeners.AutoListener;
import xyz.velocity.modules.customenchants.config.CustomEnchantConfig;
import xyz.velocity.modules.customenchants.config.saves.EnchantSave;
import xyz.velocity.modules.customenchants.config.saves.EnchantTierSave;
import xyz.velocity.modules.customenchants.config.saves.MainUISave;
import xyz.velocity.modules.customenchants.CustomEnchants;
import xyz.velocity.modules.customenchants.Exp;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@AutoListener
public class InventoryListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onClick(InventoryClickEvent e) {

        Inventory inv = e.getInventory();
        Player p = (Player) e.getWhoClicked();

        CustomEnchantConfig config = CustomEnchantConfig.getInstance();

        if (inv.getName().equals(VelocityFeatures.chat(config.getGuiName()))) {

            MainUISave mainUI = config.mainUI.stream().filter(obj -> obj.getSlot() == e.getSlot()).findFirst().orElse(null);

            if (mainUI == null) {
                e.setCancelled(true);
                return;
            }

            if (mainUI.getName().equals("shop")) {
                openInventory(p, config, "shop");
            }
            else if (mainUI.getName().equals("preview")) {
                openInventory(p, config, "preview");
            }

            e.setCancelled(true);
        }

        if (inv.getName().equals(VelocityFeatures.chat(config.getShopGuiName()))) {
            handleEnchantPurchase(e, config, p);
        }

        if (inv.getName().equals(VelocityFeatures.chat(config.getPreviewGuiName()))) {
            showEnchantList(e, config, p);
        }
    }

    private void handleEnchantPurchase(InventoryClickEvent e, CustomEnchantConfig config, Player p) {

        EnchantTierSave enchantTier = config.tiersList.stream().filter(obj -> obj.getGuiSlot() == e.getSlot()).findFirst().orElse(null);

        if (enchantTier != null) {

            if (!CustomEnchants.getInstance().hasEnoughXP(p, enchantTier.getEnchantCost()) || !enchantTier.isCanPurchase()) {
                e.setCancelled(true);
                return;
            }

            List<EnchantSave> enchants = CustomEnchants.getInstance().getEnchantsWithLevel(enchantTier.getEnchantLevel());

            if (enchants.size() < 1) {
                e.setCancelled(true);
                return;
            }

            double totalChances = 0.0;

            for (EnchantSave enchant : enchants) {
                totalChances += enchant.getChance();
            }

            int index = 0;

            for (double r = Math.random() * totalChances; index < enchants.size() - 1; ++index) {
                r -= enchants.get(index).getChance();
                if (r <= 0.0) break;
            }

            EnchantSave enchant = enchants.get(index);

            if (enchant != null) {
                p.getInventory().addItem(CustomEnchants.getInstance().buildBook(enchant));
                Exp.setTotalExperience(p, Exp.getTotalExperience(p) - enchantTier.getEnchantCost());
            }

            e.setCancelled(true);

        }

    }

    private void showEnchantList(InventoryClickEvent e, CustomEnchantConfig config, Player p) {

        EnchantTierSave tier = config.tiersList.stream().filter(obj ->
                obj.getGuiSlot() == e.getSlot()
                && e.getCurrentItem().hasItemMeta()
                && VelocityFeatures.chat(obj.getTierName()).equals(e.getCurrentItem().getItemMeta().getDisplayName())
        ).findFirst().orElse(null);

        if (tier == null) {
            e.setCancelled(true);
            return;
        }

        Inventory inv = Bukkit.createInventory(null, 27, VelocityFeatures.chat(config.getPreviewGuiName()));

        int slot = 0;

        List<EnchantSave> enchants = config.enchantList.stream().filter(obj -> obj.getEnchantTier() == tier.getEnchantLevel()).collect(Collectors.toList());

        for (EnchantSave enchant : enchants) {

            if (!enchant.isEnabled()) continue;

            String enchantLore = VelocityFeatures.chat(String.join("VDIB", enchant.getLore())
                    .replace("<current_level>", enchant.getMaxLevel() + "")
                    .replace("<success>", "0")
                    .replace("<fail>", "0")
            );

            List<String> lore = Arrays.asList(enchantLore.split("VDIB"));

            ItemStack itemStack = new ItemStack(Material.getMaterial(tier.getDisplayItem()), 1);
            ItemMeta meta = itemStack.getItemMeta();

            meta.setDisplayName(VelocityFeatures.chat(enchant.getDisplayName()));
            meta.setLore(lore);

            itemStack.setItemMeta(meta);

            inv.setItem(slot, itemStack);

            slot++;

        }

        p.openInventory(inv);

        e.setCancelled(true);
    }

    public void openInventory(Player player, CustomEnchantConfig config, String type) {

        Inventory inv;

        switch (type) {
            case "shop":
                inv = Bukkit.createInventory(null, config.getShopGuiSlots(), VelocityFeatures.chat(config.getShopGuiName()));
                addInvContents(inv, config, "shop");
                break;
            default:
                inv = Bukkit.createInventory(null, config.getPreviewGuiSlots(), VelocityFeatures.chat(config.getPreviewGuiName()));
                addInvContents(inv, config, "preview");
                break;

        }

        player.openInventory(inv);

    }

    private void addInvContents(Inventory inv, CustomEnchantConfig config, String type) {

        config.tiersList.forEach(tier -> {

            List<String> lore;

            switch (type) {
                case "shop":
                    lore = Arrays.asList(VelocityFeatures.chat(tier.getTierLore()).split("\\|"));
                    break;
                default:
                    lore = Arrays.asList(VelocityFeatures.chat(config.getPreviewTierLores().replace("<tier_name>", tier.getTierName())));
                    break;
            }

            ItemStack itemStack = new ItemStack(Material.getMaterial(tier.getDisplayItem()), 1);
            ItemMeta meta = itemStack.getItemMeta();

            meta.setDisplayName(VelocityFeatures.chat(tier.getTierName()));
            meta.setLore(lore);

            itemStack.setItemMeta(meta);

            inv.setItem(tier.getGuiSlot(), itemStack);

        });

    }

    @Getter
    private static InventoryListener instance;

    public InventoryListener() {
        instance = this;
    }

}
