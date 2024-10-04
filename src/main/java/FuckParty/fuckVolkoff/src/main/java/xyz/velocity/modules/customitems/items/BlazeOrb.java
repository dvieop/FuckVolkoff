package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.customitems.items;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;
import xyz.velocity.modules.customitems.config.PartnerItemsConfig;
import xyz.velocity.modules.customitems.config.saves.ItemSave;
import xyz.velocity.modules.customitems.PartnerItems;
import xyz.velocity.modules.customitems.annotations.Item;
import xyz.velocity.modules.util.Pair;

import java.util.ArrayList;
import java.util.Collections;

@Item
public class BlazeOrb extends AbstractItem {

    private final PartnerItemsConfig config;

    public BlazeOrb() {

        this.config = PartnerItemsConfig.getInstance();

        ItemSave item = new ItemSave(false, "blaze_orb", "&4&lBlaze Orb", "BLAZE_POWDER", 0, new ArrayList<>(Collections.singleton("Gain strenght 3")), new ArrayList<>(Collections.singletonList("INCREASE_DAMAGE:2")), 60, this.extraInfo());

        if (!config.getItems().stream().anyMatch(obj -> obj.getName().equals(item.getName()))) {
            config.getItems().add(item);
        }

    }

    @Override
    public String getName() {
        return "blaze_orb";
    }

    @Override
    public <T extends Event> void runTask(T event) {

        PlayerInteractEvent e = (PlayerInteractEvent) event;

        PartnerItems pI = PartnerItems.getInstance();
        PartnerItemsConfig config = PartnerItemsConfig.getInstance();

        String player = e.getPlayer().getUniqueId() + "_" + this.getName();

        ItemSave item = config.getItems().stream().filter(obj -> obj.getName().equals(this.getName())).findFirst().orElse(null);

        if (item == null) return;
        if (!item.isEnabled()) return;

        if (pI.isOnCooldown(player)) {
            if (System.currentTimeMillis() > pI.getCooldown(player)) {
                PartnerItems.cooldowns.remove(player);
            } else {
                pI.sendMessage(e.getPlayer(), config.onCooldownMsg, item.getDisplayName(), ((pI.getCooldown(player) - System.currentTimeMillis()) / 1000L) + "");
                return;
            }
        }

        Player p = e.getPlayer();
        Pair<Integer, Integer> itemInfo = getItemInfo(item);

        pI.handleEffects(p, item.getEffects(), itemInfo.first * 20);
        pI.sendMessage(e.getPlayer(), config.usedAbilityMsg, item.getDisplayName(), item.getCooldown() + "");

        PartnerItems.cooldowns.put(player, System.currentTimeMillis() + (item.getCooldown() * 1000L));

        pI.updateItem(p);

    }

    private JsonObject extraInfo() {
        JsonObject info = new JsonObject();

        info.addProperty("effectDuration", 20);

        info.toString();

        return info;
    }

    private Pair<Integer, Integer> getItemInfo(ItemSave item) {

        int duration = 0;
        int amplifier = 0;

        try {
            JsonObject obj = new Gson().fromJson(item.getExtra().get(0), JsonObject.class);

            duration = obj.get("effectDuration").getAsInt();
        } catch (Throwable e) {
            e.printStackTrace();
        }


        return new Pair<>(duration, amplifier);

    }

}
