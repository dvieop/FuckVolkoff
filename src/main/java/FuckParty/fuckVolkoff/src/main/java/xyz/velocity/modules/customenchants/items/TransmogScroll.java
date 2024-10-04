package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.customenchants.items;

import com.google.gson.JsonObject;
import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.velocity.VelocityFeatures;
import xyz.velocity.modules.customenchants.CustomEnchants;
import xyz.velocity.modules.customenchants.EnchantItems;
import xyz.velocity.modules.customenchants.annotations.EnchantItem;
import xyz.velocity.modules.customenchants.config.CustomEnchantConfig;
import xyz.velocity.modules.customenchants.config.saves.EnchantItemSave;
import xyz.velocity.modules.customenchants.config.saves.EnchantSave;

import java.util.Arrays;
import java.util.List;

@EnchantItem
public class TransmogScroll extends AbstractEnchantItem {

    private final CustomEnchantConfig config;

    public TransmogScroll() {

        this.config = CustomEnchantConfig.getInstance();

        EnchantItemSave item = new EnchantItemSave(true, "transmog_scroll", "&e&lTransmog Scroll", Arrays.asList("Organize enchants on your armor"), Arrays.asList(""), "PAPER", 0, true, this.extraInfo());

        if (!config.getChance().getItems().stream().anyMatch(obj -> obj.getName().equals(item.getName()))) {
            config.getChance().getItems().add(item);
        }

    }

    @Override
    public String getName() {
        return "transmog_scroll";
    }

    @Override
    public <T extends Event> void runTask(T event) {

        InventoryClickEvent e;

        try {
            e = (InventoryClickEvent) event;
        } catch (Throwable err) {
            return;
        }

        ItemStack itemStack = e.getCurrentItem();

        NBTItem nbtItem = new NBTItem(itemStack);
        NBTCompound enchantsCompound = nbtItem.getCompound("velocity_customenchants_enchants");

        if (enchantsCompound == null) {
            e.setCancelled(true);
            return;
        }

        ItemStack finished = nbtItem.getItem();

        List<String> lore = EnchantItems.getInstance().getNewLore(finished, enchantsCompound, true);

        ItemMeta itemMeta = finished.getItemMeta();
        itemMeta.setDisplayName(EnchantItems.getInstance().getTransmogDisplayName(nbtItem.getItem()));

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
