package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.masks;

import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTItem;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import kore.ArmorEquipEvent;
import lombok.Getter;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.velocity.VelocityFeatures;
import xyz.velocity.modules.armorsets.AbilityManager;
import xyz.velocity.modules.armorsets.ability.AbstractAbility;
import xyz.velocity.modules.customenchants.CustomEnchants;
import xyz.velocity.modules.masks.config.MaskConfig;
import xyz.velocity.modules.customenchants.EnchantItems;
import xyz.velocity.modules.customenchants.enchants.util.DeathWrapper;
import xyz.velocity.modules.util.ItemUtil;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class MaskListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void maskApplyEvent(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player)) return;
        if (e.getSlot() == 39) return;
        if (ItemUtil.isAirOrNull(e.getCurrentItem()) || ItemUtil.isAirOrNull(e.getCursor())) return;
        if (e.getCursor().getItemMeta() == null) return;

        NBTItem cursorIM = new NBTItem(e.getCursor());
        NBTCompound compound;

        try {
            compound = cursorIM.getCompound("velocity_custommasks_item");
        } catch (NoClassDefFoundError err) {
            return;
        }

        if (new NBTItem(e.getCurrentItem()).hasKey("velocity_custommasks_item")) return;
        if (compound == null) return;

        CustomMask mask = Masks.maskList.get(compound.getString("id"));

        if (mask == null) return;
        if (!mask.canApply(e.getCurrentItem())) {
            ((Player) e.getWhoClicked()).sendMessage(VelocityFeatures.chat(MaskConfig.getInstance().cantApply
                    .replace("<mask>", mask.maskSave.getDisplayName())
                    .replace("<item>", e.getCurrentItem().getItemMeta().hasDisplayName() ? e.getCurrentItem().getItemMeta().getDisplayName() : "")
            ));
            return;
        }

        NBTItem nbtItem = new NBTItem(e.getCurrentItem());
        NBTCompound nbtCompound = nbtItem.getCompound("velocity_custommasks_mask");

        if (nbtCompound == null) nbtCompound = nbtItem.addCompound("velocity_custommasks_mask");

        nbtCompound.setString("id", mask.maskSave.getName());

        if (mask.maskSave.getMultiMask().isEnabled()) {
            nbtCompound.setString("masks", compound.hasKey("masks") ? compound.getString("masks") : "");
        }

        ItemStack finished = nbtItem.getItem();
        ItemMeta meta = finished.getItemMeta();

        List<String> oldLore = meta.getLore() == null ? new ArrayList<>() : meta.getLore();

        oldLore.add(VelocityFeatures.chat(MaskConfig.getInstance().attachedLore
                .replace("<mask>",
                        mask.maskSave.getMultiMask().isEnabled()
                                ? Masks.getInstance().getMultiMaskLore(compound.getString("masks").split("_"))
                                : mask.maskSave.getDisplayName()
                )
        ));

        meta.setLore(oldLore);

        finished.setItemMeta(meta);

        e.setCurrentItem(finished);
        e.setCursor(null);
        e.setCancelled(true);

        ((Player) e.getWhoClicked()).playSound(e.getWhoClicked().getLocation(), Sound.ORB_PICKUP, 50.0F, 50.0F);
        ((Player) e.getWhoClicked()).sendMessage(VelocityFeatures.chat(MaskConfig.getInstance().successfullyApplied
                .replace("<mask>", mask.maskSave.getDisplayName())
                .replace("<item>", e.getCurrentItem().getItemMeta().hasDisplayName() ? e.getCurrentItem().getItemMeta().getDisplayName() : "")
        ));

    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void maskRemoveEvent(InventoryClickEvent e) {
        if (!(e.getClick() == ClickType.RIGHT)) return;
        if (e.getSlot() == 39) return;
        if (ItemUtil.isAirOrNull(e.getCurrentItem()) || !ItemUtil.isAirOrNull(e.getCursor())) return;

        NBTItem nbtItem = new NBTItem(e.getCurrentItem());
        NBTCompound nbtCompound = nbtItem.getCompound("velocity_custommasks_mask");

        if (nbtCompound == null) return;

        CustomMask mask = Masks.maskList.get(nbtCompound.getString("id"));

        if (mask == null) return;

        String appliedLore = VelocityFeatures.chat(MaskConfig.getInstance().attachedLore
                .replace("<mask>", "")
        );

        Masks.getInstance().removeLore(nbtItem.getItem(), VelocityFeatures.chat(appliedLore));

        ItemStack maskItem = mask.getItem();
        NBTItem maskNBT = new NBTItem(maskItem);

        if (nbtCompound.hasKey("masks")) {
            String[] splitList = nbtCompound.getString("masks").split("_");

            String lore = mask.maskSave.getMultiMask().getLore();

            String lookup = VelocityFeatures.chat(lore.replace("<mask>", ""));
            String replace = VelocityFeatures.chat(lore.replace("<mask>", Masks.getInstance().getMultiMaskLore(splitList)));

            maskNBT.getCompound("velocity_custommasks_item").setString("masks", nbtCompound.getString("masks"));

            Masks.getInstance().updateLore(maskNBT.getItem(), lookup, replace);

            nbtCompound.removeKey("masks");
        }

        nbtCompound.removeKey("id");
        nbtItem.removeKey("velocity_custommasks_mask");

        e.setCurrentItem(nbtItem.getItem());
        e.setCursor(maskNBT.getItem());
        e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onArmorEquip(ArmorEquipEvent event) {
        if (event.isCancelled()) return;

        Masks masks = Masks.getInstance();
        Player player = event.getPlayer();

        if(ItemUtil.hasNoItemMeta(event.getOldArmorPiece()) && ItemUtil.hasNoItemMeta(event.getNewArmorPiece()))
            return;

        masks.removeEffects(player, event.getOldArmorPiece());

        ItemStack newPiece = event.getNewArmorPiece();

        if (newPiece != null && newPiece.getType().name().endsWith("_HELMET") && masks.hasMask(newPiece)) {
            masks.equipMask(player, newPiece);
        }
    }

    @EventHandler
    public void invUpdate(InventoryClickEvent e) {
        Masks masks = Masks.getInstance();

        if (!(e.getWhoClicked() instanceof Player)) return;
        if (ItemUtil.isAirOrNull(e.getCurrentItem())) return;
        if (e.getCurrentItem() == null) return;
        if (e.getSlot() != 39) return;

        Player player = (Player) e.getWhoClicked();
        UUID id = player.getUniqueId();

        if (e.getCurrentItem() != null && masks.hasMask(e.getCurrentItem())) {
            ItemStack current = e.getCurrentItem();

            NBTItem nbtItem = new NBTItem(current);
            NBTCompound nbtCompound = nbtItem.getCompound("velocity_custommasks_mask");

            if (nbtCompound == null) return;

            CustomMask mask = Masks.maskList.get(nbtCompound.getString("id"));

            if (mask == null) return;

            if (Masks.equippedMasks.containsKey(id)) {
                e.setCurrentItem(Masks.equippedMasks.get(id));
                Masks.equippedMasks.remove(id);
            } else {
                e.setCurrentItem(Masks.getInstance().createOriginal(current));
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onJoin(PlayerJoinEvent event) {
        Masks masks = Masks.getInstance();
        Player player = event.getPlayer();

        ItemStack helmetItem = player.getInventory().getHelmet();

        if (helmetItem != null && helmetItem.getType().name().endsWith("_HELMET") && masks.hasMask(helmetItem)) {
            masks.equipMask(player, helmetItem);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onLeave(PlayerQuitEvent event) {
        Masks.getInstance().unequipMask(event.getPlayer());
    }

    @EventHandler
    public void blockPlace(BlockPlaceEvent e) {
        ItemStack item = e.getItemInHand();

        if (!ItemUtil.isAirOrNull(item) && (Masks.getInstance().hasItem(item) || Masks.getInstance().hasMask(item))) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onEXP(PlayerExpChangeEvent e) {
        Masks masks = Masks.getInstance();

        Player p = e.getPlayer();
        ItemStack helmet = p.getInventory().getHelmet();

        if (helmet != null && masks.hasMask(helmet)) {
            CustomMask mask = masks.getMask(helmet);
            String effect = masks.getEffect("XPMULTI", mask.maskSave);

            if (effect == null) return;

            double multiplier = Double.parseDouble(effect);

            e.setAmount((int) (e.getAmount() * multiplier));
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void editLoot(PlayerDeathEvent e) {
        Masks.getInstance().editLoot(e);
    }

    /*@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onDeath(PlayerDeathEvent e) {
        if (e.getEntity().getKiller() == null || !(e.getEntity().getKiller() instanceof Player)) return;

        Masks masks = Masks.getInstance();
        Player killer = e.getEntity().getKiller();
        ItemStack helmet = killer.getInventory().getHelmet();

        if (helmet != null && masks.hasMask(helmet)) {
            CustomMask mask = masks.getMask(helmet);
            DeathWrapper deathWrapper = EnchantItems.toReturn.get(e.getEntity().getUniqueId());

            if (deathWrapper == null) return;

            String effect = masks.getEffect("NEGATE", mask.maskSave);

            if (effect == null) return;

            int chance = Integer.parseInt(effect);

            List<ItemStack> getItems = deathWrapper.getItems();

            for (Iterator<ItemStack> iterator = getItems.iterator(); iterator.hasNext();) {
                iterator.next();
                int newChance = ThreadLocalRandom.current().nextInt(100);

                if (newChance < chance) {
                    iterator.remove();
                }
            }

            deathWrapper.setItems(getItems);

            ListIterator<ItemStack> list = e.getDrops().listIterator();

            while(list.hasNext()) {
                ItemStack item = list.next();

                if (deathWrapper.getItems().contains(item)) {
                    list.remove();
                }
            }
        }
    }*/

    @EventHandler
    public void entityDamage(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player)) return;
        if (!e.getCause().equals(EntityDamageEvent.DamageCause.POISON)) return;

        Masks masks = Masks.getInstance();
        Player player = (Player) e.getEntity();

        ItemStack helmetItem = player.getInventory().getHelmet();

        if (helmetItem != null && masks.hasMask(helmetItem)) {
            CustomMask mask = Masks.getInstance().getMask(helmetItem);
            String effect = Masks.getInstance().getEffect("IMMUNITY", mask.maskSave);

            if (effect == null) return;

            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void maskItemMerge(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player)) return;
        if (e.getSlot() == 39) return;
        if (ItemUtil.isAirOrNull(e.getCurrentItem()) || ItemUtil.isAirOrNull(e.getCursor())) return;

        NBTItem toAttachItem = new NBTItem(e.getCursor());
        NBTCompound toAttachCompound = toAttachItem.getCompound("velocity_custommasks_item");

        NBTItem multiMaskItem = new NBTItem(e.getCurrentItem());
        NBTCompound multiMaskCompound = multiMaskItem.getCompound("velocity_custommasks_item");

        if (toAttachCompound == null || multiMaskCompound == null) return;

        CustomMask mask = Masks.maskList.get(multiMaskCompound.getString("id"));

        if (mask == null) return;
        if (!mask.maskSave.getMultiMask().isEnabled()) return;

        String masks = "";
        if (multiMaskCompound.hasKey("masks")) masks = multiMaskCompound.getString("masks");

        String attachedMaskID = toAttachCompound.getString("id");
        String addToMasks = masks.isEmpty() ? attachedMaskID : "_" + attachedMaskID;

        CustomMask maskToAttach = Masks.maskList.get(attachedMaskID);

        if (maskToAttach.maskSave.getMultiMask().isEnabled()) return;

        masks += addToMasks;

        multiMaskCompound.setString("masks", masks);

        ItemStack finished = multiMaskItem.getItem();

        String[] splitList = masks.split("_");

        if (splitList.length > mask.maskSave.getMultiMask().getMax()) return;

        String lore = mask.maskSave.getMultiMask().getLore();

        String lookup = VelocityFeatures.chat(lore.replace("<mask>", ""));
        String replace = VelocityFeatures.chat(lore.replace("<mask>", Masks.getInstance().getMultiMaskLore(splitList)));

        Masks.getInstance().updateLore(finished, lookup, replace);

        e.setCurrentItem(finished);
        e.setCursor(null);
        e.setCancelled(true);

        ((Player) e.getWhoClicked()).playSound(e.getWhoClicked().getLocation(), Sound.ORB_PICKUP, 50.0F, 50.0F);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDamage(EntityDamageByEntityEvent e) {
        if (!(e.getEntity() instanceof Player)) return;
        if (!(e.getDamager() instanceof  Player)) return;

        Player attacker = (Player) e.getDamager();

        if (!Masks.equippedMasks.containsKey(attacker.getUniqueId())) return;

        ItemStack helmet = attacker.getEquipment().getHelmet();

        if (helmet == null) return;

        NBTItem nbtItem = new NBTItem(helmet);
        NBTCompound nbtCompound = nbtItem.getCompound("velocity_custommasks_mask");

        CustomMask mask = Masks.maskList.get(nbtCompound.getString("id"));

        if (mask == null) return;

        String effect = Masks.getInstance().getEffect("CURSEDMARK", mask.maskSave);

        if (effect == null) return;

        AbilityManager.abilityList.get("cursedmark").runTask(e);
    }

    @Getter
    private static MaskListener instance;

    public MaskListener() {
        instance = this;
    }

}
