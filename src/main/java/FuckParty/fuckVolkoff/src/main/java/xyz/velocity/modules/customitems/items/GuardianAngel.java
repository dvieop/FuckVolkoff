package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.customitems.items;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import xyz.velocity.modules.customitems.config.PartnerItemsConfig;
import xyz.velocity.modules.customitems.config.saves.ItemSave;
import xyz.velocity.modules.customitems.PartnerItems;
import xyz.velocity.modules.customitems.annotations.Item;

import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;

@Item
public class GuardianAngel extends AbstractItem {

    public static Object2ObjectOpenHashMap<UUID, Long> hasGuardianAngel = new Object2ObjectOpenHashMap<>();
    private final PartnerItemsConfig config;

    public GuardianAngel() {

        this.config = PartnerItemsConfig.getInstance();

        ItemSave item = new ItemSave(false, "guardian_angel", "&b&lGuardian Angel", "FEATHER", 0, new ArrayList<>(Collections.singleton("Regens your hearts when you were supposed to die")), new ArrayList<>(Collections.singleton("")), 60, this.extraInfo());

        if (!config.getItems().stream().anyMatch(obj -> obj.getName().equals(item.getName()))) {
            config.getItems().add(item);
        }

    }

    @Override
    public String getName() {
        return "guardian_angel";
    }

    @Override
    public <T extends Event> void runTask(T event) {

        if (event instanceof EntityDamageByEntityEvent) {
            damageEvent((EntityDamageByEntityEvent) event);
        }

        if (event instanceof PlayerInteractEvent) {
            interactEvent((PlayerInteractEvent) event);
        }

    }

    private void damageEvent(EntityDamageByEntityEvent e) {

        Player violated = (Player) e.getEntity();

        if (!hasGuardianAngel.containsKey(violated.getUniqueId())) return;
        if (System.currentTimeMillis() > hasGuardianAngel.get(violated.getUniqueId())) {
            hasGuardianAngel.remove(violated.getUniqueId());
            return;
        }

        if((violated.getHealth() - e.getFinalDamage()) <= 1) {
            violated.setHealth(violated.getMaxHealth());

            hasGuardianAngel.remove(violated.getUniqueId());
            //e.setCancelled(true);
        }

    }

    private void interactEvent(PlayerInteractEvent e) {

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
        int duration = getInformation(item);

        PartnerItems.cooldowns.put(player, System.currentTimeMillis() + (item.getCooldown() * 1000L));
        hasGuardianAngel.put(p.getUniqueId(), System.currentTimeMillis() + (duration * 1000L));

        pI.sendMessage(p, config.usedAbilityMsg, item.getDisplayName(), item.getCooldown() + "");
        pI.updateItem(p);

    }

    private JsonObject extraInfo() {
        JsonObject info = new JsonObject();

        info.addProperty("expiresAfter", 20);

        info.toString();

        return info;
    }

    private int getInformation(ItemSave item) {

        int expiresAt = 0;

        try {
            JsonObject obj = new Gson().fromJson(item.getExtra().get(0), JsonObject.class);

            expiresAt = obj.get("expiresAfter").getAsInt();
        } catch (Throwable e) {
            e.printStackTrace();
        }


        return expiresAt;

    }

}
