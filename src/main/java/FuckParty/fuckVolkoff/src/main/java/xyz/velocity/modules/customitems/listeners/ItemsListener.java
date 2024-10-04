package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.customitems.listeners;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTItem;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import xyz.velocity.VelocityFeatures;
import xyz.velocity.bukkitutils.listeners.AutoListener;
import xyz.velocity.modules.customenchants.CustomEnchants;
import xyz.velocity.modules.customitems.config.PartnerItemsConfig;
import xyz.velocity.modules.customitems.config.saves.ItemSave;
import xyz.velocity.modules.customitems.ItemManager;
import xyz.velocity.modules.customitems.items.ExoticBone;
import xyz.velocity.modules.customitems.items.IceFlower;
import xyz.velocity.modules.customitems.items.PrePearl;
import xyz.velocity.modules.customitems.items.SwitcherBall;
import xyz.velocity.modules.util.ItemUtil;

@AutoListener
public class ItemsListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onUse(PlayerInteractEvent e) {

        if (e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {

            NBTItem item;

            try {
                item = new NBTItem(e.getItem());
            } catch (Throwable throwable) {
                return;
            }

            NBTCompound compound = item.getCompound("velocity_customItems_item");

            if (compound == null) return;

            ItemManager.itemList.get(compound.getString("id")).runTask(e);

            e.setCancelled(true);

        }

    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onHit(EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof Player)) return;
        if (!(e.getEntity() instanceof Player)) return;

        Player p = (Player) e.getDamager();

        if (ItemUtil.isAirOrNull(p.getItemInHand())) return;

        NBTItem item = new NBTItem(p.getItemInHand());
        NBTCompound compound = item.getCompound("velocity_customItems_item");

        if (compound == null) return;

        switch (compound.getString("id")) {
            case "exotic_bone":
                ItemManager.itemList.get("exotic_bone").runTask(e);
                break;
            case "ice_flower":
                ItemManager.itemList.get("ice_flower").runTask(e);
                break;
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void guardianAngel(EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof Player)) return;
        if (!(e.getEntity() instanceof Player)) return;

        ItemManager.itemList.get("guardian_angel").runTask(e);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlace(BlockPlaceEvent e) {
        if (ExoticBone.bonedPlayers.containsKey(e.getPlayer().getUniqueId())) {
            long time = ExoticBone.bonedPlayers.get(e.getPlayer().getUniqueId());

            if (System.currentTimeMillis() >= time) {
                ExoticBone.bonedPlayers.remove(e.getPlayer().getUniqueId());
                return;
            }

            e.getPlayer().sendMessage(VelocityFeatures.chat(
                    PartnerItemsConfig.getInstance().getCantPlaceDueToBone()
                            .replace("<duration>", (time - System.currentTimeMillis()) / 1000L + "")
            ).split("/n"));

            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onSnowball(EntityDamageByEntityEvent e) {
        if (e.isCancelled()) return;
        if (!(e.getDamager() instanceof Snowball)) return;

        ItemManager.itemList.get("switcher_ball").runTask(e);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onThrow(ProjectileLaunchEvent e) {
        if (!(e.getEntity() instanceof Snowball)) return;
        if (!(e.getEntity().getShooter() instanceof Player)) return;

        Player p = (Player) e.getEntity().getShooter();

        NBTItem item = new NBTItem(p.getItemInHand());
        NBTCompound compound = item.getCompound("velocity_customItems_item");

        if (compound != null && compound.getString("id").equals("switcher_ball")) {
            SwitcherBall.switchers.put(p, e.getEntity().getUniqueId());
        }

    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPearlThrow(ProjectileLaunchEvent e) {

        if (!(e.getEntity() instanceof EnderPearl)) return;
        if (!(e.getEntity().getShooter() instanceof Player)) return;

        Player p = (Player) e.getEntity().getShooter();

        NBTItem item = new NBTItem(p.getItemInHand());
        NBTCompound compound = item.getCompound("velocity_customItems_item");

        if (compound != null && compound.getString("id").equals("pre_pearl")) {
            PrePearl.thrownPearls.put(p, e.getEntity().getUniqueId());
        }

    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onLand(ProjectileHitEvent e) {
        if (!(e.getEntity() instanceof EnderPearl)) return;
        if (!(e.getEntity().getShooter() instanceof Player)) return;

        ItemManager.itemList.get("pre_pearl").runTask(e);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onTeleport(PlayerTeleportEvent e) {

        if (e.getCause() == PlayerTeleportEvent.TeleportCause.ENDER_PEARL) {
            if (PrePearl.thrownPearls.containsKey(e.getPlayer())) {
                e.setCancelled(true);
            }
        }

    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onFrozenHit(EntityDamageByEntityEvent e) {
        if (!(e.getEntity() instanceof Player)) return;

        Player p = (Player) e.getEntity();

        if (IceFlower.frozenPlayers.containsKey(p)) {
            long cd = IceFlower.frozenPlayers.get(p);

            if (System.currentTimeMillis() > cd) {
                IceFlower.frozenPlayers.remove(p);
                return;
            }

            ItemSave item = PartnerItemsConfig.getInstance().items.stream().filter(obj -> obj.getName().equals("ice_flower")).findFirst().orElse(null);

            if (item == null) return;

            try {
                JsonObject obj = new Gson().fromJson(item.getExtra().get(0), JsonObject.class);

                double amplifier = obj.get("damageMultiplier").getAsDouble();

                e.setDamage(e.getDamage() * amplifier);
            } catch (Throwable err) {
                err.printStackTrace();
            }
        }
    }

    @Getter
    private static ItemsListener instance;

    public ItemsListener() {
        instance = this;
    }

}
