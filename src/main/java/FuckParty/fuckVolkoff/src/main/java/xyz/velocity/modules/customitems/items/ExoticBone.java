package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.customitems.items;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import xyz.velocity.modules.customitems.config.PartnerItemsConfig;
import xyz.velocity.modules.customitems.config.saves.ItemSave;
import xyz.velocity.modules.customitems.PartnerItems;
import xyz.velocity.modules.customitems.annotations.Item;
import xyz.velocity.modules.customitems.items.util.ExoticBoneWrapper;
import xyz.velocity.modules.util.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;

@Item
public class ExoticBone extends AbstractItem {

    private static Object2ObjectOpenHashMap<UUID, ExoticBoneWrapper> boneHits = new Object2ObjectOpenHashMap<>();
    public static Object2ObjectOpenHashMap<UUID, Long> bonedPlayers = new Object2ObjectOpenHashMap<>();
    private final PartnerItemsConfig config;

    public ExoticBone() {

        this.config = PartnerItemsConfig.getInstance();

        ItemSave item = new ItemSave(false, "exotic_bone", "&f&lExotic Bone", "BONE", 0, new ArrayList<>(Collections.singleton("Make people not place blocks")), new ArrayList<>(Collections.singleton("")), 60, this.extraInfo());

        if (!config.getItems().stream().anyMatch(obj -> obj.getName().equals(item.getName()))) {
            config.getItems().add(item);
        }

    }

    @Override
    public String getName() {
        return "exotic_bone";
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

        String player = damager.getUniqueId() + "_" + this.getName();

        ItemSave item = config.getItems().stream().filter(obj -> obj.getName().equals(this.getName())).findFirst().orElse(null);

        if (item == null) return;
        if (!item.isEnabled()) return;

        if (pI.isOnCooldown(player)) {
            if (System.currentTimeMillis() > pI.getCooldown(player)) {
                PartnerItems.cooldowns.remove(player);
            } else {
                pI.sendMessage(damager, config.onCooldownMsg, item.getDisplayName(), ((pI.getCooldown(player) - System.currentTimeMillis()) / 1000L) + "");
                return;
            }
        }

        checkAndPut(damager, violated);

        ExoticBoneWrapper ebw = boneHits.get(damager.getUniqueId());

        Pair<Integer, Integer> info = getInformation(item);

        if (System.currentTimeMillis() > ebw.getSinceLastHit()) {
            ebw.setHits(0);
            ebw.setSinceLastHit(System.currentTimeMillis());
        }

        ebw.setHits(ebw.getHits() + 1);

        if (ebw.getHits() >= info.second) {

            pI.sendMessage(damager, config.effectPlayer, item.getDisplayName(), item.getCooldown() + "", violated.getName());
            pI.sendMessage(violated, config.gotAffected, item.getDisplayName(), item.getCooldown() + "", damager.getName(), info.first + "");

            bonedPlayers.put(violated.getUniqueId(), System.currentTimeMillis() + (info.first * 1000L));
            PartnerItems.cooldowns.put(player, System.currentTimeMillis() + (item.getCooldown() * 1000L));

            pI.updateItem(damager);

        }

    }

    private JsonObject extraInfo() {
        JsonObject info = new JsonObject();

        info.addProperty("effectDuration", 20);
        info.addProperty("hitsToEffect", 3);

        info.toString();

        return info;
    }

    private Pair<Integer, Integer> getInformation(ItemSave item) {

        int duration = 0;
        int hits = 0;

        try {
            JsonObject obj = new Gson().fromJson(item.getExtra().get(0), JsonObject.class);

            duration = obj.get("effectDuration").getAsInt();
            hits = obj.get("hitsToEffect").getAsInt();
        } catch (Throwable e) {
            e.printStackTrace();
        }


        return new Pair<>(duration, hits);

    }

    private void checkAndPut(Player player, Player player2) {

        if (!boneHits.containsKey(player.getUniqueId())) {
            boneHits.put(player.getUniqueId(), new ExoticBoneWrapper(player2, 0, System.currentTimeMillis() + 5000L));
        }

    }

}
