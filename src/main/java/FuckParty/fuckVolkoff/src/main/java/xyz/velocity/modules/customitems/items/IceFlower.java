package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.customitems.items;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import xyz.velocity.VelocityFeatures;
import xyz.velocity.modules.customitems.config.PartnerItemsConfig;
import xyz.velocity.modules.customitems.config.saves.ItemSave;
import xyz.velocity.modules.customitems.PartnerItems;
import xyz.velocity.modules.customitems.annotations.Item;
import xyz.velocity.modules.util.Pair;

import java.util.ArrayList;
import java.util.Collections;

@Item
public class IceFlower extends AbstractItem {

    private final PartnerItemsConfig config;
    public static Object2ObjectOpenHashMap<Player, Long> frozenPlayers = new Object2ObjectOpenHashMap<>();

    public IceFlower() {

        this.config = PartnerItemsConfig.getInstance();

        ItemSave item = new ItemSave(false, "ice_flower", "&b&lIce Flower", "BLAZE_POWDER", 0, new ArrayList<>(Collections.singleton("Freeze player you hit")), new ArrayList<>(Collections.singleton("SLOW:9")), 60, this.extraInfo());

        if (!config.getItems().stream().anyMatch(obj -> obj.getName().equals(item.getName()))) {
            config.getItems().add(item);
        }

    }

    @Override
    public String getName() {
        return "ice_flower";
    }

    @Override
    public <T extends Event> void runTask(T event) {

        EntityDamageByEntityEvent e;

        try {
            e = (EntityDamageByEntityEvent) event;
        } catch (Throwable throwable) {
            return;
        }

        PartnerItems pI = PartnerItems.getInstance();

        Player damager = (Player) e.getDamager();
        Player violated = (Player) e.getEntity();

        String id = damager.getUniqueId() + "_" + this.getName();

        ItemSave item = config.getItems().stream().filter(obj -> obj.getName().equals(this.getName())).findFirst().orElse(null);

        if (item == null) return;
        if (!item.isEnabled()) return;

        if (pI.isOnCooldown(id)) {
            if (System.currentTimeMillis() > pI.getCooldown(id)) {
                PartnerItems.cooldowns.remove(id);
            } else {
                damager.sendMessage(VelocityFeatures.chat(
                        config.onCooldownMsg
                                .replace("<ability>", item.getDisplayName())
                                .replace("<cooldown>", ((pI.getCooldown(id) - System.currentTimeMillis()) / 1000L) + "")
                ).split("/n"));
                return;
            }
        }

        Pair<Integer, Double> itemInfo = getItemInfo(item);

        pI.handleEffects(violated, item.getEffects(), itemInfo.first * 20);

        PartnerItems.cooldowns.put(id, System.currentTimeMillis() + (item.getCooldown() * 1000L));

        pI.sendMessage(damager, config.usedAbilityMsg, item.getDisplayName(), item.getCooldown() + "");
        pI.sendMessage(violated, config.gotAffected, item.getDisplayName(), item.getCooldown() + "", damager.getName(), itemInfo.first + "");

        frozenPlayers.put(violated, System.currentTimeMillis() + (itemInfo.first * 1000L));

        pI.updateItem(damager);

    }

    private JsonObject extraInfo() {
        JsonObject info = new JsonObject();

        info.addProperty("effectDuration", 20);
        info.addProperty("damageMultiplier", 0.95);

        info.toString();

        return info;
    }

    private Pair<Integer, Double> getItemInfo(ItemSave item) {

        int duration = 0;
        double amplifier = 0;

        try {
            JsonObject obj = new Gson().fromJson(item.getExtra().get(0), JsonObject.class);

            duration = obj.get("effectDuration").getAsInt();
            amplifier = obj.get("damageMultiplier").getAsDouble();
        } catch (Throwable e) {
            e.printStackTrace();
        }


        return new Pair<>(duration, amplifier);

    }

}
