package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.customitems.items;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import xyz.velocity.modules.customenchants.CustomEnchants;
import xyz.velocity.modules.customitems.config.PartnerItemsConfig;
import xyz.velocity.modules.customitems.config.saves.ItemSave;
import xyz.velocity.modules.customitems.PartnerItems;
import xyz.velocity.modules.customitems.annotations.Item;

import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;

@Item
public class SwitcherBall extends AbstractItem {

    public static Object2ObjectMap<Player, UUID> switchers = new Object2ObjectOpenHashMap<>();
    private final PartnerItemsConfig config;

    public SwitcherBall() {

        this.config = PartnerItemsConfig.getInstance();

        ItemSave item = new ItemSave(false, "switcher_ball", "&f&lSwitcher Ball", "SNOW_BALL", 0, new ArrayList<>(Collections.singleton("Regens your hearts when you were supposed to die")), new ArrayList<>(Collections.singleton("")), 60, this.extraInfo());

        if (!config.getItems().stream().anyMatch(obj -> obj.getName().equals(item.getName()))) {
            config.getItems().add(item);
        }

    }

    @Override
    public String getName() {
        return "switcher_ball";
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

        Snowball s = (Snowball) e.getDamager();

        ItemSave item = PartnerItemsConfig.getInstance().getItems().stream().filter(obj -> obj.getName().equals(this.getName())).findFirst().orElse(null);

        if (item == null) return;
        if (!item.isEnabled()) return;

        if (s.getShooter() instanceof Player) {
            Player p = (Player) s.getShooter();

            if (!switchers.containsKey(p)) return;
            if (!switchers.get(p).equals(s.getUniqueId())) return;

            if (e.getEntity() instanceof Player) {
                Player violated = (Player) e.getEntity();

                Location l1 = p.getLocation();
                Location l2 = violated.getLocation();

                double maxDistance = getInformation(item);
                double distance = l1.distance(l2);

                if (distance >= maxDistance) return;
                if (!CustomEnchants.getInstance().canDamage(p, violated)) return;
                if (CustomEnchants.getInstance().isAlly(p, violated)) return;

                p.teleport(l2);
                violated.teleport(l1);

                PartnerItems.getInstance().sendMessage(p, config.effectPlayer, item.getDisplayName(), item.getCooldown() + "", violated.getName());
                PartnerItems.getInstance().sendMessage(violated, config.gotSwitchered, item.getDisplayName(), item.getCooldown() + "", p.getName());

                PartnerItems.cooldowns.put(p.getUniqueId() + "_" + this.getName(), System.currentTimeMillis() + item.getCooldown() * 1000L);

                PartnerItems.getInstance().updateItem(p, item.getDisplayName());

            }
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

                e.setCancelled(true);
                return;
            }
        }

    }

    private JsonObject extraInfo() {
        JsonObject info = new JsonObject();

        info.addProperty("maxDistance", 20);

        info.toString();

        return info;
    }

    private int getInformation(ItemSave item) {

        int distance = 0;

        try {
            JsonObject obj = new Gson().fromJson(item.getExtra().get(0), JsonObject.class);

            distance = obj.get("maxDistance").getAsInt();
        } catch (Throwable e) {
            e.printStackTrace();
        }


        return distance;

    }

}
