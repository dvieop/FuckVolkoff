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
public class BlackScroll extends AbstractEnchantItem {

    private final CustomEnchantConfig config;

    public BlackScroll() {

        this.config = CustomEnchantConfig.getInstance();

        EnchantItemSave item = new EnchantItemSave(true, "black_scroll", "&8&lBlack Scroll", Arrays.asList("Removes random enchant from piece"), Arrays.asList(""), "INK_SACK", 0, true, this.extraInfo());

        if (!config.getChance().getItems().stream().anyMatch(obj -> obj.getName().equals(item.getName()))) {
            config.getChance().getItems().add(item);
        }

    }

    @Override
    public String getName() {
        return "black_scroll";
    }

    @Override
    public <T extends Event> void runTask(T event) {

        InventoryClickEvent e;

        try {
            e = (InventoryClickEvent) event;
        } catch (Throwable err) {
            return;
        }

        Player player = (Player) e.getWhoClicked();
        ItemStack itemStack = e.getCurrentItem();

        NBTItem nbtItem = new NBTItem(itemStack);
        NBTItem blackScroll = new NBTItem(e.getCursor());

        NBTCompound enchantsCompound = nbtItem.getCompound("velocity_customenchants_enchants");
        NBTCompound scrollCompound = blackScroll.getCompound("velocity_enchantItem_item");

        if (enchantsCompound == null || enchantsCompound.getKeys().isEmpty()) {
            e.setCancelled(true);
            return;
        }

        int success = scrollCompound.getInteger("success");
        int fail = scrollCompound.getInteger("fail");

        if (!EnchantItems.getInstance().checkChances(e, itemStack, success, fail)) {
            e.setCancelled(true);
            EnchantItems.getInstance().updateItem(e);

            return;
        }

        List<EnchantSave> enchantList = EnchantItems.getInstance().getEnchants(enchantsCompound);
        EnchantSave randomEnchant = CustomEnchants.getInstance().getRandomEnchant(enchantList);

        if (randomEnchant == null) return;

        int level = enchantsCompound.getInteger(randomEnchant.getName());
        enchantsCompound.removeKey(randomEnchant.getName());

        ItemStack newItem = nbtItem.getItem();
        ItemStack book = CustomEnchants.getInstance().buildBook(randomEnchant, level, success, fail);

        List<String> lore = EnchantItems.getInstance().getNewLore(newItem, enchantsCompound, false);

        ItemMeta itemMeta = newItem.getItemMeta();

        itemMeta.setLore(lore);
        newItem.setItemMeta(itemMeta);

        e.setCurrentItem(newItem);
        e.setCancelled(true);

        player.getInventory().addItem(book);
        player.sendMessage(VelocityFeatures.chat(CustomEnchantConfig.getInstance().blackScrollSuccess
                .replace("<enchant>", randomEnchant.getDisplayName())
        ));

        EnchantItems.getInstance().updateItem(e);

    }

    private JsonObject extraInfo() {
        JsonObject info = new JsonObject();

        info.toString();

        return info;
    }

}
