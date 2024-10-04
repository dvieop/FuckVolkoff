package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.customenchants.items;

import com.google.gson.JsonObject;
import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Material;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.velocity.VelocityFeatures;
import xyz.velocity.modules.customenchants.EnchantItems;
import xyz.velocity.modules.customenchants.annotations.EnchantItem;
import xyz.velocity.modules.customenchants.config.CustomEnchantConfig;
import xyz.velocity.modules.customenchants.config.saves.EnchantItemSave;

import java.util.Arrays;
import java.util.List;

@EnchantItem
public class HeroicArmor extends AbstractEnchantItem {

    private final CustomEnchantConfig config;

    public HeroicArmor() {

        this.config = CustomEnchantConfig.getInstance();

        EnchantItemSave item = new EnchantItemSave(true, "heroic_armor", "&d&lHeroic Upgrade &7(Armor)", Arrays.asList("Enhance your armor with heroic upgrade"), Arrays.asList(""), "NETHER_STAR", 0, true, this.extraInfo());

        if (!config.getChance().getItems().stream().anyMatch(obj -> obj.getName().equals(item.getName()))) {
            config.getChance().getItems().add(item);
        }

    }

    @Override
    public String getName() {
        return "heroic_armor";
    }

    @Override
    public <T extends Event> void runTask(T event) {

        InventoryClickEvent e;

        try {
            e = (InventoryClickEvent) event;
        } catch (Throwable err) {
            return;
        }

        String compound = "velocity_enchantItem_heroicArmor";

        NBTItem cursorIM = new NBTItem(e.getCursor());
        NBTCompound itemCompound = cursorIM.getCompound("velocity_enchantItem_item");

        ItemStack itemStack = e.getCurrentItem();

        NBTItem nbtItem = new NBTItem(itemStack);
        NBTCompound enchantItemCompound = nbtItem.getCompound(compound);
        NBTCompound enchantsCompound = nbtItem.getCompound("velocity_customenchants_enchants");

        if (enchantItemCompound == null) enchantItemCompound = nbtItem.addCompound(compound);
        if (enchantsCompound == null) enchantsCompound = nbtItem.addCompound("velocity_customenchants_enchants");

        EnchantItemSave enchantItemSave = CustomEnchantConfig.getInstance().chance.getItems().stream().filter(obj -> obj.getName().equals(itemCompound.getString("id"))).findFirst().orElse(null);

        if (enchantItemSave == null) return;
        if (!enchantItemSave.isEnabled()) return;

        if (enchantItemCompound.getBoolean("heroicArmor")) return;
        if (!EnchantItems.getInstance().isArmor(nbtItem.getItem())) return;

        enchantItemCompound.setString("id", this.getName());
        enchantItemCompound.setBoolean("heroicArmor", true);
        EnchantItems.getInstance().updateDisplayName(nbtItem, enchantItemSave);

        ItemStack finished = nbtItem.getItem();

        List<String> lore = EnchantItems.getInstance().getNewLore(finished, enchantsCompound, false);

        ItemMeta itemMeta = finished.getItemMeta();

        if (!enchantItemSave.getAppliedLore().isEmpty()) {
            String toAdd = VelocityFeatures.chat(String.join("VDIB", enchantItemSave.getAppliedLore()));

            lore.addAll(Arrays.asList(toAdd.split("VDIB")));
        }

        itemMeta.setLore(lore);
        itemMeta.spigot().setUnbreakable(true);
        finished.setItemMeta(itemMeta);

        String type = finished.getType().name().split("_")[1];
        finished.setType(Material.getMaterial("GOLD_" + type));

        e.setCurrentItem(finished);
        e.setCancelled(true);

        EnchantItems.getInstance().updateItem(e);

    }

    private JsonObject extraInfo() {
        JsonObject info = new JsonObject();

        info.addProperty("displayName", "&5&k|||&d Heroic &5&k|||&d <original>");
        info.addProperty("damageReduce", 0.015);

        info.toString();

        return info;
    }

}
