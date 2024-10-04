package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.armorsets;

import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTItem;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import kore.ArmorEquipEvent;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import xyz.velocity.modules.armorsets.ability.AbstractAbility;
import xyz.velocity.modules.armorsets.config.saves.ItemSave;
import xyz.velocity.modules.armorsets.config.SpecialItemsConfig;
import xyz.velocity.modules.customenchants.CustomEnchants;
import xyz.velocity.modules.customenchants.EnchantItems;
import xyz.velocity.modules.customenchants.enchants.util.DeathWrapper;
import xyz.velocity.modules.util.ItemUtil;

import java.util.UUID;

public class ArmorListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onArmorEquip(ArmorEquipEvent event) {
        ArmorSets armorSets = ArmorSets.getInstance();
        Player player = event.getPlayer();

        if(ItemUtil.hasNoItemMeta(event.getOldArmorPiece()) && ItemUtil.hasNoItemMeta(event.getNewArmorPiece())) return;

        armorSets.removeEffects(player, event.getOldArmorPiece());

        ItemStack newPiece = event.getNewArmorPiece();

        if (ItemUtil.isAirOrNull(newPiece)) return;
        if (newPiece != null && armorSets.isSetPiece(newPiece)) {
            armorSets.equipSet(player, newPiece);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDamage(EntityDamageByEntityEvent e) {
        if (!(e.getEntity() instanceof Player)) return;
        if (!(e.getDamager() instanceof  Player)) return;

        Player attacker = (Player) e.getDamager();
        Player defender = (Player) e.getEntity();

        if (!CustomEnchants.getInstance().canDamage(attacker, defender)) return;
        if (CustomEnchants.getInstance().isAlly(attacker, defender)) return;

        for (Object2ObjectMap.Entry<String, AbstractAbility> map : AbilityManager.abilityList.object2ObjectEntrySet()) {
            map.getValue().runTask(e);
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        ItemStack helmet = player.getInventory().getHelmet();

        if (helmet == null) return;

        NBTItem nbtItem = new NBTItem(helmet);
        NBTCompound nbtCompound = nbtItem.getCompound("velocity_armorsets_armor");

        if (nbtCompound == null) return;

        String id = nbtCompound.getString("id");

        if (ArmorSets.getInstance().hasFullSet(player.getInventory(), id)) {
            ArmorSets.getInstance().equipSet(player, helmet);
        }
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        if (ArmorSets.equippedSets.containsKey(event.getPlayer().getUniqueId())) {
            ArmorSets.getInstance().removeEffects(event.getPlayer(), event.getPlayer().getInventory().getHelmet());
        }
    }

    @EventHandler
    public void entityDamage(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player)) return;
        if (!e.getCause().equals(EntityDamageEvent.DamageCause.POISON)) return;

        Player player = (Player) e.getEntity();

        UUID id = player.getUniqueId();

        if (ArmorSets.equippedSets.containsKey(id)) {
            CustomSet set = ArmorSets.setsCache.get(ArmorSets.equippedSets.get(id));

            if (set == null) return;

            String effect = ArmorSets.getInstance().getEffect("IMMUNITY", set.armorSave);

            if (effect == null) return;

            e.setCancelled(true);
        }
    }

    /*@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onDeath(PlayerDeathEvent e) {
        if (e.getEntity().getKiller() == null || !(e.getEntity().getKiller() instanceof Player)) return;

        ArmorSets armorSets = ArmorSets.getInstance();
        Player killer = e.getEntity().getKiller();
        UUID uuid = killer.getUniqueId();

        if (ArmorSets.equippedSets.containsKey(uuid)) {
            CustomSet customSet = ArmorSets.setsCache.get(ArmorSets.equippedSets.get(uuid));
            DeathWrapper deathWrapper = EnchantItems.toReturn.get(e.getEntity().getUniqueId());

            if (customSet == null || deathWrapper == null) return;

            String effect = armorSets.getEffect("NEGATE", customSet.armorSave);

            if (effect == null) return;

            int chance = Integer.parseInt(effect);

            armorSets.handleNegate(chance, deathWrapper, e);
        }

        if (ItemUtil.isAirOrNull(killer.getItemInHand())) return;

        ItemStack weapon = killer.getItemInHand();

        NBTItem item = new NBTItem(weapon);
        NBTCompound compound = item.getCompound("velocity_armorsets_item");

        if (compound == null) return;

        String id = compound.getString("id");

        DeathWrapper deathWrapper = EnchantItems.toReturn.get(e.getEntity().getUniqueId());
        ItemSave weaponSave = SpecialItemsConfig.getInstance().items.stream().filter(obj -> obj.getName().equals(id)).findFirst().orElse(null);

        if (weaponSave == null || deathWrapper == null) return;
        if (weaponSave.getArmorsetBind().isEnabled() && weaponSave.getArmorsetBind().getArmor() != ArmorSets.equippedSets.get(uuid)) return;
        if (!weaponSave.getCustomEffects().isEnabled()) return;

        String effect = armorSets.getEffect("NEGATE", weaponSave);
        int chance = Integer.parseInt(effect);

        armorSets.handleNegate(chance, deathWrapper, e);
    }*/

    @EventHandler
    public void armorStand(PlayerArmorStandManipulateEvent e) {
        if (e.getRightClicked().getCustomName().equals("diamondSwordAnimation")) e.setCancelled(true);
    }

    @Getter
    private static ArmorListener instance;

    public ArmorListener() {
        instance = this;
    }

}
