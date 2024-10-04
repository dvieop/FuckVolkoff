package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.customenchants.items;

import com.google.gson.JsonObject;
import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.enchantments.Enchantment;
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
public class DragonsHeart extends AbstractEnchantItem {

    private final CustomEnchantConfig config;

    public DragonsHeart() {

        this.config = CustomEnchantConfig.getInstance();

        EnchantItemSave item = new EnchantItemSave(true, "dragons_heart", "&5&lDragons Heart", Arrays.asList("Increase your sharpness by 1"), Arrays.asList(""), "NETHER_BRICK_ITEM", 0, true, this.extraInfo());

        if (!config.getChance().getItems().stream().anyMatch(obj -> obj.getName().equals(item.getName()))) {
            config.getChance().getItems().add(item);
        }

    }

    @Override
    public String getName() {
        return "dragons_heart";
    }

    @Override
    public <T extends Event> void runTask(T event) {

        InventoryClickEvent e;

        try {
            e = (InventoryClickEvent) event;
        } catch (Throwable err) {
            return;
        }

        String compound = "velocity_enchantItem_dragonsHeart";

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

        if (enchantItemCompound.getBoolean("dragonsHeart")) return;
        if (!EnchantItems.getInstance().isWeapon(nbtItem.getItem())) return;

        enchantItemCompound.setBoolean("dragonsHeart", true);
        EnchantItems.getInstance().addEnchantsToItem(nbtItem.getItem(), Enchantment.DAMAGE_ALL, 5, true);

        ItemStack finished = nbtItem.getItem();

        List<String> lore = EnchantItems.getInstance().getNewLore(finished, enchantsCompound, false);

        ItemMeta itemMeta = finished.getItemMeta();

        if (!enchantItemSave.getAppliedLore().isEmpty()) {
            String toAdd = VelocityFeatures.chat(String.join("VDIB", enchantItemSave.getAppliedLore()));

            lore.addAll(Arrays.asList(toAdd.split("VDIB")));
        }

        itemMeta.setLore(lore);
        finished.setItemMeta(itemMeta);

        e.setCurrentItem(finished);
        e.setCancelled(true);

        EnchantItems.getInstance().updateItem(e);

    }

    private JsonObject extraInfo() {
        JsonObject info = new JsonObject();

        info.toString();

        return info;
    }

}
