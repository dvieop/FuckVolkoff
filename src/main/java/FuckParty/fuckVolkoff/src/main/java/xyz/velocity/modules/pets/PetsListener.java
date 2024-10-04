package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.pets;

import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTItem;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.velocity.VelocityFeatures;
import xyz.velocity.modules.masks.CustomMask;
import xyz.velocity.modules.masks.Masks;
import xyz.velocity.modules.pets.commands.InventorySubCommand;
import xyz.velocity.modules.pets.config.PetsConfig;
import xyz.velocity.modules.pets.config.StatsConfig;
import xyz.velocity.modules.pets.config.saves.*;
import xyz.velocity.modules.util.InventoryUtil;
import xyz.velocity.modules.util.ItemUtil;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class PetsListener implements Listener {

    @EventHandler
    private void playerJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();

        StatsSave statsSave = StatsConfig.getInstance().getPlayerPets(p);

        if (statsSave.getEquippedPet() == null) return;

        Pets.equippedPets.put(p.getUniqueId(), new PetWrapper(p, Pets.petList.get(statsSave.getEquippedPet().getName()), statsSave.getEquippedPet()));
    }

    @EventHandler
    private void playerLeave(PlayerQuitEvent e) {
        UUID id = e.getPlayer().getUniqueId();

        if (Pets.equippedPets.containsKey(id)) {
            PetWrapper petWrapper = Pets.equippedPets.get(id);

            Pets.getInstance().removePet(e.getPlayer(), petWrapper);
        }
    }

    @EventHandler
    private void playerTeleport(PlayerTeleportEvent e) {
        UUID id = e.getPlayer().getUniqueId();
        if (Pets.equippedPets.containsKey(id)) {
            PetWrapper petWrapper = Pets.equippedPets.get(id);

            if (System.currentTimeMillis() < petWrapper.getTime()) return;

            petWrapper.hologram.delete();
            petWrapper.spawnPet();
        }

        if (PetsConfig.getInstance().blacklistedWorlds.contains(e.getTo().getWorld().getName())) {
            Pets.getInstance().unequipPet(e.getPlayer());

            e.getPlayer().getActivePotionEffects().clear();
        }
    }

    @EventHandler
    private void playerClick(PlayerInteractEvent e) {
        if (!(e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) && !(e.getAction().equals(Action.RIGHT_CLICK_AIR))) return;

        Player p = e.getPlayer();
        ItemStack itemStack = e.getPlayer().getItemInHand();

        if (ItemUtil.isAirOrNull(itemStack)) return;
        if (itemStack.getType() != Material.SKULL_ITEM) return;

        NBTItem nbtItem = new NBTItem(itemStack);
        NBTCompound nbtCompound = nbtItem.getCompound("velocity_pets_item");

        if (nbtCompound == null) return;

        e.setCancelled(true);

        String id = nbtCompound.getString("id");

        CustomPet pet = Pets.petList.get(id);

        if (pet == null) return;

        StatsSave statsSave = StatsConfig.getInstance().getPlayerPets(p);

        PetStats hasPet = statsSave.getPetInventory().stream().filter(obj -> obj.getName().equals(pet.getPetSave().getName())).findFirst().orElse(null);

        if (hasPet != null) {
            p.sendMessage(VelocityFeatures.chat(PetsConfig.getInstance().alreadyHavePet));
            return;
        }

        if (statsSave.getPetInventory().size() == statsSave.getSlots()) {
            p.sendMessage(VelocityFeatures.chat(PetsConfig.getInstance().invFull));
            return;
        }

        PetSave petSave = pet.getPetSave();
        PetTierSave petTierSave = PetsConfig.getInstance().petTiers.stream().filter(obj -> obj.getTier() == petSave.getTier()).findFirst().orElse(null);

        if (petTierSave == null) {
            return;
        }

        int level;
        int xp;
        int xpTillLevelup;
        double multiplier = 1.0;

        if (nbtCompound.getInteger("level") > 0) {
            level = nbtCompound.getInteger("level");
            xp = nbtCompound.getInteger("xp");
            xpTillLevelup = nbtCompound.getInteger("xpToLevelUp");
        } else {
            level = 1;
            xp = 0;
            xpTillLevelup = (int) Pets.getInstance().calculateXP(petTierSave, 1);
        }

        if (nbtCompound.getDouble("xpshard") > 1.0) {
            multiplier = nbtCompound.getDouble("xpshard");
        }

        statsSave.getPetInventory().add(new PetStats(petSave.getName(), level, xp, xpTillLevelup, multiplier));

        StatsConfig.getInstance().getData().put(p.getUniqueId(), statsSave);
        //StatsConfig.getInstance().saveData();

        p.sendMessage(VelocityFeatures.chat(PetsConfig.getInstance().petAdded
                .replace("<pet>", petSave.getDisplayName())
                .replace("<level>", level + "")
        ));

        Pets.getInstance().updateItem(p, itemStack);
    }

    @EventHandler
    public void equipPet(InventoryClickEvent e) {
        Inventory inv = e.getInventory();
        Player p = (Player) e.getWhoClicked();

        PetsConfig config = PetsConfig.getInstance();

        if (!inv.getName().equals(VelocityFeatures.chat(config.getInventory().getGuiName()))) return;

        e.setCancelled(true);

        if (ItemUtil.isAirOrNull(e.getCurrentItem())) return;
        if (e.getCurrentItem().getType() != Material.SKULL_ITEM) return;

        ItemStack clickedItem = e.getCurrentItem();

        NBTItem nbtItem = new NBTItem(clickedItem);

        NBTCompound nbtCompound = nbtItem.getCompound("velocity_pets_item");

        if (nbtCompound == null) return;

        String id = nbtCompound.getString("id");

        CustomPet customPet = Pets.petList.get(id);

        StatsSave statsSave = StatsConfig.getInstance().getPlayerPets(p);

        PetStats clickedPet = statsSave.getPetInventory().stream().filter(
                obj -> obj.getName().equals(customPet.getPetSave().getName())
        ).findFirst().orElse(null);
        PetStats equippedPet = statsSave.getEquippedPet();

        if (clickedPet == null) return;

        if (clickedPet.equals(equippedPet)) {
            p.sendMessage(VelocityFeatures.chat(PetsConfig.getInstance().alreadyEquipped));
            return;
        }

        if (equippedPet != null) {
            PetWrapper petWrapper = Pets.equippedPets.get(p.getUniqueId());

            if (petWrapper != null) {
                //petWrapper.petStand.runnable.cancel();
                petWrapper.deleteHologram();
            }
        }

        PetWrapper petWrapper = new PetWrapper(p, Pets.petList.get(clickedPet.getName()), clickedPet);

        Pets.equippedPets.put(p.getUniqueId(), petWrapper);

        statsSave.setEquippedPet(clickedPet);

        StatsConfig.getInstance().getData().put(p.getUniqueId(), statsSave);
        //StatsConfig.getInstance().saveData();

        p.sendMessage(VelocityFeatures.chat(PetsConfig.getInstance().equippedPet
                .replace("<pet>", customPet.petSave.getDisplayName())
                .replace("<level>", clickedPet.getLevel() + "")
        ));

        Pets.getInstance().updatePetVisibility(petWrapper);

        //p.closeInventory();
        InventorySubCommand.reloadInventory(p, inv);
    }

    @EventHandler
    public void miscHandle(InventoryClickEvent e) {
        Inventory inv = e.getInventory();
        Player p = (Player) e.getWhoClicked();
        UUID id = p.getUniqueId();

        PetsConfig config = PetsConfig.getInstance();

        if (!inv.getName().equals(VelocityFeatures.chat(config.getInventory().getGuiName()))) return;

        e.setCancelled(true);

        if (ItemUtil.isAirOrNull(e.getCurrentItem())) return;

        PetWrapper petWrapper = Pets.equippedPets.get(id);

        if (e.getSlot() == config.inventory.getUnEquipItem().getSlot()) {
            if (!Pets.equippedPets.containsKey(id)) return;

            p.sendMessage(VelocityFeatures.chat(config.unEquip
                    .replace("<pet>", petWrapper.customPet.petSave.getDisplayName().replace("<level>", petWrapper.petStats.getLevel() + ""))
            ));

            Pets.getInstance().removePet(p, petWrapper);

            StatsSave statsSave = StatsConfig.getInstance().getPlayerPets(p);

            statsSave.setEquippedPet(null);

            StatsConfig.getInstance().getData().put(p.getUniqueId(), statsSave);
            //StatsConfig.getInstance().saveData();

            //p.closeInventory();
            InventorySubCommand.reloadInventory(p, inv);
        }

        if (e.getSlot() == config.getInventory().getTakeOutItem().getSlot()) {
            if (!Pets.equippedPets.containsKey(id)) return;
            if (InventoryUtil.isFull(p.getInventory())) {
                p.sendMessage(VelocityFeatures.chat(config.playerInvFull));

                return;
            }

            ItemStack item = petWrapper.customPet.getItem();

            NBTItem nbtItem = new NBTItem(item);

            NBTCompound nbtCompound = nbtItem.getCompound("velocity_pets_item");

            if (nbtCompound == null) return;

            nbtCompound.setInteger("level", petWrapper.petStats.getLevel());
            nbtCompound.setInteger("xp" , petWrapper.petStats.getXp());
            nbtCompound.setInteger("xpToLevelUp" , petWrapper.petStats.getXpToLevelUp());
            nbtCompound.setDouble("xpshard", petWrapper.petStats.getXpBoost());

            p.getInventory().addItem(Pets.getInstance().updateItemLore(nbtItem.getItem(), petWrapper.customPet.petSave, petWrapper.petStats));
            p.sendMessage(VelocityFeatures.chat(config.takeOut
                    .replace("<pet>", petWrapper.customPet.petSave.getDisplayName().replace("<level>", petWrapper.petStats.getLevel() + ""))
            ));

            Pets.getInstance().removePet(p, petWrapper);

            StatsSave statsSave = StatsConfig.getInstance().getPlayerPets(p);

            statsSave.setEquippedPet(null);
            statsSave.getPetInventory().removeIf(obj -> obj.getName().equals(petWrapper.petStats.getName()));

            StatsConfig.getInstance().getData().put(p.getUniqueId(), statsSave);
            //StatsConfig.getInstance().saveData();

            //p.closeInventory();
            InventorySubCommand.reloadInventory(p, inv);
        }

        if (e.getSlot() == config.getInventory().getToggleItem().getSlot()) {
            boolean status = Pets.getInstance().isPetToggled(p);

            String toggle = !status ? "&aenabled" : "&cdisabled";

            p.sendMessage(VelocityFeatures.chat(config.visibilityToggle
                    .replace("<status>", toggle)
            ));

            Pets.toggledPets.put(id, !status);

            for (Object2ObjectMap.Entry<UUID, PetWrapper> entry : Pets.equippedPets.object2ObjectEntrySet()) {
                boolean getStatus = Pets.toggledPets.get(id);

                if (getStatus) {
                    entry.getValue().hologram.setShowPlayer(p);
                    entry.getValue().hologram.removeHidePlayer(p);
                } else {
                    entry.getValue().hologram.setHidePlayer(p);
                    entry.getValue().hologram.removeShowPlayer(p);
                }
            }

            //p.closeInventory();
            InventorySubCommand.reloadInventory(p, inv);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDamage(EntityDamageByEntityEvent e) {

        if (e.getDamager() instanceof Player) {
            if (!Pets.getInstance().getPetType((Player) e.getDamager()).equals("COMBAT")) return;

            Pets.getInstance().handleEffects((Player) e.getDamager(), e, true);
            if (e.getEntity() instanceof Player) Pets.getInstance().handleDebuff((Player) e.getDamager(), (Player) e.getEntity());
        }

        if (e.getEntity() instanceof Player) {
            if (!Pets.getInstance().getPetType((Player) e.getEntity()).equals("COMBAT")) return;

            Pets.getInstance().handleEffects((Player) e.getEntity(), e, false);
            Pets.getInstance().handleLowHP((Player) e.getEntity());
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        UUID id = e.getPlayer().getUniqueId();
        if (!Pets.equippedPets.containsKey(id)) return;

        PetWrapper petWrapper = Pets.equippedPets.get(id);

        if (!petWrapper.customPet.getPetSave().getType().equals("FARMING")) return;

        String type = "FARM:" + e.getBlock().getType().name();

        XpSave xpSave = Pets.getInstance().getXpSaveFromEffect(petWrapper, type);

        if (xpSave == null) return;

        Pets.getInstance().handleXP(e.getPlayer(), petWrapper, xpSave);
    }

    @EventHandler
    public void onDeath(EntityDeathEvent e) {
        if (e.getEntity().getKiller() == null) return;
        
        UUID id = e.getEntity().getKiller().getUniqueId();

        if (!Pets.equippedPets.containsKey(id)) return;

        PetWrapper petWrapper = Pets.equippedPets.get(id);

        if (e.getEntity() instanceof Player) {
            XpSave xp = Pets.getInstance().getXpSaveFromEffect(petWrapper, "KILL:PLAYER");

            Pets.getInstance().handleXP(e.getEntity().getKiller(), petWrapper, xp);
        } else {
            XpSave xp = Pets.getInstance().getXpSaveFromEffect(petWrapper, "KILL:" + e.getEntity().getType().name());

            Pets.getInstance().handleXP(e.getEntity().getKiller(), petWrapper, xp);
        }
    }

    @EventHandler
    public void onEXP(PlayerExpChangeEvent e) {
        Player p = e.getPlayer();
        UUID id  = p.getUniqueId();

        if (!Pets.equippedPets.containsKey(id)) return;

        PetWrapper petWrapper = Pets.equippedPets.get(id);

        LevelEffectSave effects =  Pets.getInstance().getEffectsByLevel(petWrapper.customPet.getPetSave().getLevelEffects(), petWrapper.petStats);

        if (effects == null) return;

        for (String effect : effects.getEffects()) {
            if (effect.startsWith("exp")) {
                String[] split = effect.split(":");

                String xp = split[1];

                e.setAmount((int) (e.getAmount() * Double.parseDouble(xp)));
            }
        }
    }

    @EventHandler
    public void pearlLand(ProjectileHitEvent e) {
        if (!(e.getEntity() instanceof EnderPearl)) return;
        if (!(e.getEntity().getShooter() instanceof Player)) return;

        Player p = (Player) e.getEntity().getShooter();

        if (!Pets.equippedPets.containsKey(p.getUniqueId())) return;

        Pets.getInstance().handlePearlLand(p);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void attachableItemApply(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player)) return;
        if (ItemUtil.isAirOrNull(e.getCurrentItem()) || ItemUtil.isAirOrNull(e.getCursor())) return;
        if (e.getCursor().getItemMeta() == null) return;
        if (e.getCurrentItem() == null) return;

        NBTItem xpShard = new NBTItem(e.getCursor());
        NBTCompound xpShardCompound = xpShard.getCompound("velocity_pets_attachable");

        if (xpShardCompound == null) return;
        if (!Pets.getInstance().isPet(e.getCurrentItem())) return;

        NBTItem petItem = new NBTItem(e.getCurrentItem());
        NBTCompound petCompound = petItem.getCompound("velocity_pets_item");

        String id = xpShardCompound.getString("id");
        double multiplier = xpShardCompound.getDouble("multiplier");

        AttachableItemSave ais = PetsConfig.getInstance().attachableItems.stream().filter(obj -> obj.getName().equals(id)).findFirst().orElse(null);

        if (ais == null) return;
        if (petCompound.getDouble("xpshard") > 1.0) return;

        petCompound.setDouble("xpshard", multiplier);

        ItemStack finishedPet = petItem.getItem();
        ItemMeta itemMeta = finishedPet.getItemMeta();

        List<String> originalLore = itemMeta.getLore();

        originalLore.add("&7 ");
        originalLore.add(ais.getAttachedLore()
                .replace("<multiplier>", multiplier + "")
        );

        String lore = VelocityFeatures.chat(String.join("VDIB", originalLore));

        itemMeta.setLore(Arrays.asList(lore.split("VDIB")));
        finishedPet.setItemMeta(itemMeta);

        e.setCurrentItem(finishedPet);
        e.setCancelled(true);
        Pets.getInstance().updateItem(e);
    }

    @Getter
    private static PetsListener instance;

    public PetsListener() {
        instance = this;
    }

}
