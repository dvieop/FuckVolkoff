package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.customenchants.listeners;

import com.golfing8.kore.FactionsKore;
import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTItem;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import kore.ArmorEquipEvent;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import xyz.velocity.bukkitutils.listeners.AutoListener;
import xyz.velocity.modules.GlobalDamageModifier;
import xyz.velocity.modules.customenchants.*;
import xyz.velocity.modules.customenchants.config.CustomEnchantConfig;
import xyz.velocity.modules.customenchants.enchants.AbstractEnchant;
import xyz.velocity.modules.customenchants.enchants.util.DeathWrapper;
import xyz.velocity.modules.customenchants.events.CustomDeathEvent;
import xyz.velocity.modules.customenchants.events.EnchantProcEvent;
import xyz.velocity.modules.util.ItemUtil;

import java.util.Iterator;

@AutoListener
public class EnchantListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onCustomEnchantApply(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player)) return;
        if (ItemUtil.isAirOrNull(e.getCurrentItem()) || ItemUtil.isAirOrNull(e.getCursor())) return;
        if (e.getCursor().getItemMeta() == null) return;

        NBTItem cursorIM = new NBTItem(e.getCursor());
        NBTCompound compound = CustomEnchants.getInstance().getCompound(cursorIM);

        if (compound == null) return;

        switch (compound.getName()) {
            case "velocity_customenchants_book":
                EnchantItems.getInstance().applyEnchant(e);
                break;
            case "velocity_enchantItem_item":
                EnchantItems.getInstance().applyItem(e);
                break;
        }
    }

    @EventHandler
    public void onArmorEquip(ArmorEquipEvent event) {
        CustomEnchants cE = CustomEnchants.getInstance();

        if(ItemUtil.hasNoItemMeta(event.getOldArmorPiece()) && ItemUtil.hasNoItemMeta(event.getNewArmorPiece()))
            return;

        cE.runArmorEquip(event);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        CustomEnchants.getInstance().handleEnchants(event.getPlayer(), true);
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        CustomEnchants.getInstance().handleEnchants(event.getPlayer(), false);
        ((CraftPlayer) event.getPlayer()).getHandle().setAbsorptionHearts((float) 0);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void pvpEntityHit(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) return;
        if (!(event.getEntity() instanceof Player)) return;
        if (!CustomEnchants.getInstance().canDamage((Player) event.getDamager(), (Player) event.getEntity())) return;
        if (CustomEnchants.getInstance().isAlly((Player) event.getDamager(), (Player) event.getEntity())) return;

        for (Object2ObjectMap.Entry<String, AbstractEnchant> map : EnchantManager.nonVanillaEnchants.object2ObjectEntrySet()) {
            if (!map.getValue().isEnabled()) continue;
            if (!(map.getValue().getEnchantType() == EnumEnchantType.PVP)) continue;

            map.getValue().runTask(event);
        }

        GlobalDamageModifier.setDamageModifiers(event);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void mobEntityHit(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) return;
        if (event.getEntity() instanceof Player) return;

        for (Object2ObjectMap.Entry<String, AbstractEnchant> map : EnchantManager.nonVanillaEnchants.object2ObjectEntrySet()) {
            if (!map.getValue().isEnabled()) continue;
            if (!(map.getValue().getEnchantType() == EnumEnchantType.GRINDING)) continue;

            map.getValue().runTask(event);
        }
    }

    @EventHandler
    public void entityDeath(EntityDeathEvent e) {
        if (e.getEntity() == null || e.getEntity().getKiller() == null) return;
        if (!(e.getEntity().getKiller() instanceof Player) || (e.getEntity() instanceof Player)) return;

        for (Object2ObjectMap.Entry<String, AbstractEnchant> map : EnchantManager.nonVanillaEnchants.object2ObjectEntrySet()) {
            if (!map.getValue().isEnabled()) continue;
            if (!(map.getValue().getEnchantType() == EnumEnchantType.GRINDING)) continue;

            map.getValue().runTask(e);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onFallDamage(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player)) return;
        if (e.getCause() != EntityDamageEvent.DamageCause.FALL) return;

        EnchantManager.nonVanillaEnchants.get("JellyLegs").runTask(e);
    }

    @EventHandler
    public void onBlockHit(BlockDamageEvent e) {
        if (e.getPlayer().getItemInHand() == null) return;
        if (e.getPlayer().getItemInHand().getType().name().endsWith("_PICKAXE")) {
            EnchantManager.nonVanillaEnchants.get("ObsidianBreaker").runTask(e);
        }
    }

    @EventHandler
    public void bowShot(EntityShootBowEvent e) {
        if (!(e.getEntity() instanceof Player)) return;
        if (!(e.getProjectile() instanceof Arrow)) return;

        for (Object2ObjectMap.Entry<String, AbstractEnchant> map : EnchantManager.nonVanillaEnchants.object2ObjectEntrySet()) {
            if (!map.getValue().isEnabled()) continue;
            if (!(map.getValue().getEnchantType() == EnumEnchantType.PROJECTILE)) continue;

            map.getValue().runTask(e);
        }
    }

    @EventHandler
    public void arrowHit(ProjectileHitEvent e) {
        if (!(e.getEntity() instanceof Arrow)) return;
        if (!(e.getEntity().getShooter() instanceof Player)) return;

        for (Object2ObjectMap.Entry<String, AbstractEnchant> map : EnchantManager.nonVanillaEnchants.object2ObjectEntrySet()) {
            if (!map.getValue().isEnabled()) continue;
            if (!(map.getValue().getEnchantType() == EnumEnchantType.PROJECTILE)) continue;

            map.getValue().runTask(e);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void arrowDamage(EntityDamageByEntityEvent e) {
        if (!(e.getEntity() instanceof Player)) return;
        if (e.getCause() != EntityDamageEvent.DamageCause.PROJECTILE) return;
        if (!(e.getDamager() instanceof Arrow)) return;
        if (!(((Arrow) e.getDamager()).getShooter() instanceof Player)) return;
        if (!CustomEnchants.getInstance().canDamage((Player) (((Arrow) e.getDamager()).getShooter()), (Player) e.getEntity())) return;
        if (CustomEnchants.getInstance().isAlly((Player) (((Arrow) e.getDamager()).getShooter()), (Player) e.getEntity())) return;

        for (Object2ObjectMap.Entry<String, AbstractEnchant> map : EnchantManager.nonVanillaEnchants.object2ObjectEntrySet()) {
            if (!map.getValue().isEnabled()) continue;
            if (!(map.getValue().getEnchantType() == EnumEnchantType.PROJECTILE)) continue;

            map.getValue().runTask(e);
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void deathEventItems(PlayerDeathEvent e) {
        //EnchantItems.getInstance().deathEvent(e);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void deathEvent(PlayerDeathEvent e) {
        if (e.getEntity().getKiller() != null) {
            CustomDeathEvent event = new CustomDeathEvent(e.getEntity(), e);
            Bukkit.getServer().getPluginManager().callEvent(event);

            if (e.getEntity().getWorld().getName().equalsIgnoreCase("arcade")) event.setCancelled(true);
            if (!event.isCancelled()) {
                event.loadNegatedItems();

                EnchantManager.nonVanillaEnchants.get("Slayer").runTask(e);

                Iterator<ItemStack> list = e.getDrops().listIterator();

                while(list.hasNext()) {
                    ItemStack item = list.next();
                    DeathWrapper deathWrapper = CustomDeathEvent.itemsList.get(e.getEntity().getUniqueId());

                    if (deathWrapper != null && deathWrapper.getItems().contains(item)) {
                        list.remove();
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void respawnEvent(PlayerRespawnEvent e) {
        e.getPlayer().setMaxHealth(20);
        Player p = e.getPlayer();

        if (CustomDeathEvent.itemsList.containsKey(p.getUniqueId())) {
            DeathWrapper deathWrapper = CustomDeathEvent.itemsList.get(p.getUniqueId());

            if (System.currentTimeMillis() > deathWrapper.getTimeout()) {
                CustomDeathEvent.itemsList.remove(p.getUniqueId());
                return;
            }

            for (ItemStack item : deathWrapper.getItems()) {
                NBTItem nbtItem = new NBTItem(item);
                NBTCompound compound = nbtItem.getCompound("velocity_enchantItem_holyWhiteScroll");

                if (compound == null) continue;

                compound.setBoolean("deathProtection", false);

                EnchantItems.getInstance().removeLore(
                        nbtItem.getItem(),
                        CustomEnchantConfig.getInstance().chance.getItems().stream().filter(obj -> obj.getName().equals("holy_white_scroll")).findFirst().orElse(null).getAppliedLore()
                );

                p.getInventory().addItem(nbtItem.getItem());
            }

            CustomDeathEvent.itemsList.remove(p.getUniqueId());
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        NBTItem item = new NBTItem(event.getItemInHand());
        NBTCompound compound = item.getCompound("velocity_enchantItem_item");

        if (compound != null) event.setCancelled(true);
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent e) {
        CustomEnchants.getInstance().handleEnchants(e.getPlayer(), true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEXP(PlayerExpChangeEvent e) {
        EnchantManager.nonVanillaEnchants.get("Inquisitive").runTask(e);
    }

    @Getter
    private static EnchantListener instance;

    public EnchantListener() {
        instance = this;
    }

}
