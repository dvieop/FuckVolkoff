package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.customenchants.items;

import com.google.gson.JsonObject;
import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.InventoryClickEvent;
import xyz.velocity.modules.armorsets.ArmorSets;
import xyz.velocity.modules.customenchants.EnchantItems;
import xyz.velocity.modules.customenchants.annotations.EnchantItem;
import xyz.velocity.modules.customenchants.config.CustomEnchantConfig;
import xyz.velocity.modules.customenchants.config.saves.EnchantItemSave;

import java.util.Arrays;
import java.util.UUID;

@EnchantItem
public class BloodSenseOrb extends AbstractEnchantItem {

    private final CustomEnchantConfig config;

    public BloodSenseOrb() {

        this.config = CustomEnchantConfig.getInstance();

        EnchantItemSave item = new EnchantItemSave(true, "bloodsense", "&f&lAbility Orb: &4&lBlood Sense", Arrays.asList("Make enemies bleed"), Arrays.asList(""), "MAGMA_CREAM", 0, true, this.extraInfo());

        if (!config.getChance().getItems().stream().anyMatch(obj -> obj.getName().equals(item.getName()))) {
            config.getChance().getItems().add(item);
        }

    }

    @Override
    public String getName() {
        return "bloodsense";
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
        UUID id = player.getUniqueId();

        if (e.getSlot() != 39 && e.getSlot() != 38 && e.getSlot() != 37 && e.getSlot() != 36) return;
        if (!ArmorSets.equippedSets.containsKey(id)) return;

        NBTItem cursorIM = new NBTItem(e.getCursor());
        NBTCompound itemCompound = cursorIM.getCompound("velocity_enchantItem_item");

        EnchantItems.getInstance().applyAbilityOrb(e, player, itemCompound);
    }

    private JsonObject extraInfo() {
        JsonObject info = new JsonObject();

        info.addProperty("lookupString", "&c&lLocked");
        info.addProperty("replaceString", "&a&lUnlocked");
        info.addProperty("setBind", "lucifer");

        info.toString();

        return info;
    }

}
