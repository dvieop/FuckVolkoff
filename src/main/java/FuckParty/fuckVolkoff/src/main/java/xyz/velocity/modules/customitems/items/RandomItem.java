package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.customitems.items;

import com.google.gson.JsonObject;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import xyz.velocity.modules.customitems.config.PartnerItemsConfig;
import xyz.velocity.modules.customitems.config.saves.ItemSave;
import xyz.velocity.modules.customitems.PartnerItems;
import xyz.velocity.modules.customitems.annotations.Item;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Item
public class RandomItem extends AbstractItem {

    private final PartnerItemsConfig config;

    public RandomItem() {

        this.config = PartnerItemsConfig.getInstance();

        ItemSave item = new ItemSave(false, "random_item", "&d&lRandom Item", "REDSTONE_BLOCK", 0, new ArrayList<>(Collections.singleton("Receive a random partner item")), new ArrayList<>(Collections.singleton("")), 0, new JsonObject());

        if (!config.getItems().stream().anyMatch(obj -> obj.getName().equals(item.getName()))) {
            config.getItems().add(item);
        }

    }

    @Override
    public String getName() {
        return "random_item";
    }

    @Override
    public <T extends Event> void runTask(T event) {

        PlayerInteractEvent e = (PlayerInteractEvent) event;

        PartnerItems pI = PartnerItems.getInstance();
        PartnerItemsConfig config = PartnerItemsConfig.getInstance();

        ItemSave item = config.getItems().stream().filter(obj -> obj.getName().equals(this.getName())).findFirst().orElse(null);
        List<ItemSave> items = config.getItems().stream().filter(obj -> obj.isEnabled()).collect(Collectors.toList());

        if (item == null) return;
        if (!item.isEnabled()) return;

        double totalChances = 0.0;

        for (ItemSave itemSave : config.getItems()) {
            if (itemSave.getName().equals(this.getName())) continue;
            if (!itemSave.isEnabled()) continue;

            totalChances += 20;
        }

        int index = 0;

        for (double r = Math.random() * totalChances; index < items.size() - 1; ++index) {
            r -= 20;
            if (r <= 0.0) break;
        }

        ItemSave itemToGive = items.get(index);

        if (itemToGive != null) {
            ItemStack itemStack = PartnerItems.getInstance().buildItem(itemToGive.getName(), 1);

            if (itemStack == null) return;

            e.getPlayer().getInventory().addItem(PartnerItems.getInstance().addGlow(itemStack));

            pI.updateItem(e.getPlayer());
        }

    }

}
