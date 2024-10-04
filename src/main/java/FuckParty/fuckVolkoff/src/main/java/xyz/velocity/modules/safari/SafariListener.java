package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.safari;

import com.earth2me.essentials.Essentials;
import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTItem;
import lombok.Getter;
import net.ess3.api.MaxMoneyException;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import xyz.velocity.VelocityFeatures;
import xyz.velocity.modules.fishing.config.saves.ItemMultiplierSave;
import xyz.velocity.modules.safari.config.SafariConfig;
import xyz.velocity.modules.safari.config.saves.DropItemSave;
import xyz.velocity.modules.safari.config.saves.PriorityRewardSave;
import xyz.velocity.modules.safari.config.saves.SpecialRewardSave;
import xyz.velocity.modules.util.EnchantUtil;
import xyz.velocity.modules.util.ItemUtil;
import xyz.velocity.modules.util.Location;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class SafariListener implements Listener {

    @EventHandler
    public void onEggHit(PlayerInteractEvent e) {
        if (!(e.getAction().equals(Action.RIGHT_CLICK_BLOCK))) return;
        if (e.getClickedBlock().getType() != Material.DRAGON_EGG) return;

        SafariConfig config = SafariConfig.getInstance();

        Player player = e.getPlayer();

        SafariCache safari = Safari.safariCache.stream().filter(obj -> obj.location.equals(e.getClickedBlock().getLocation())).findFirst().orElse(null);

        if (safari == null) return;

        e.setCancelled(true);

        if (safari.isActive()) {
            player.sendMessage(VelocityFeatures.chat(config.safariActive));

            return;
        }

        if (safari.rewardAvailable) return;
        if (!safari.cooldownExpired()) {
            player.sendMessage(VelocityFeatures.chat(
                    config.safariCooldown
                        .replace("<cooldown>", safari.cooldownLeft())
            ));

            return;
        }

        if (Safari.getInstance().isAtLimit(player, safari.getSafariTierSave())) {
            player.sendMessage(VelocityFeatures.chat(SafariConfig.getInstance().limitReached));

            return;
        }
        if (!Safari.getInstance().canOpenSafari(player, safari.getSafariTierSave())) {
            player.sendMessage(VelocityFeatures.chat(SafariConfig.getInstance().noPermission));

            return;
        }

        safari.setActive(true);
        safari.setStarter(player);

        safari.hologram.updateHologram("ongoing");

        Safari.getInstance().spawnMobs(safari, player);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onDamage(EntityDamageByEntityEvent e) {
        if (!Safari.mobCache.containsKey(e.getDamager().getUniqueId())) return;

        MobCache mobCache = Safari.mobCache.get(e.getDamager().getUniqueId());

        if (!(e.getEntity() instanceof Player) && mobCache.summoner != e.getEntity()) {
            try {
                ((Creature) e.getEntity()).setTarget(mobCache.summoner);
            } catch (Throwable ignored) {

            }
            e.setCancelled(true);
        }

        int damage = Safari.mobCache.get(e.getDamager().getUniqueId()).getDamage();

        e.setDamage(e.getDamage() + damage);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onMobDamage(EntityDamageByEntityEvent e) {
        if (!Safari.mobCache.containsKey(e.getEntity().getUniqueId())) return;

        MobCache mobCache = Safari.mobCache.get(e.getEntity().getUniqueId());

        if (!(e.getDamager() instanceof Player) && mobCache.summoner != e.getDamager()) e.setCancelled(true);

        double health = Safari.getInstance().roundAvoid(((LivingEntity) e.getEntity()).getHealth(), 0);

        e.getEntity().setCustomName(VelocityFeatures.chat(mobCache.original
                .replace("<level>", mobCache.level + "")
                .replace("<health>", health + "")
        ));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void entityDeath(EntityDeathEvent e) {
        if (!Safari.mobCache.containsKey(e.getEntity().getUniqueId())) return;

        UUID id = e.getEntity().getUniqueId();
        MobCache mobCache = Safari.mobCache.get(id);

        if (mobCache == null) return;

        e.getDrops().clear();

        SafariCache safariCache = mobCache.getSafari();

        if (safariCache.mobEntities.contains(e.getEntity())) {
            safariCache.mobEntities.remove(e.getEntity());

            Player player = e.getEntity().getKiller();

            if (player.getItemInHand() != null) {
                ItemStack sword = player.getItemInHand();

                NBTItem nbtItem = new NBTItem(sword);
                NBTCompound compound = nbtItem.getCompound("velocity_safari_sword");

                if (compound != null) {
                    Safari.getInstance().updateScore(player, nbtItem, false, true);
                    Safari.getInstance().updateLore(nbtItem, player);
                }

                player.setItemInHand(nbtItem.getItem());
                Safari.getInstance().handleXP(player, mobCache.getMobSave().getXp());
            }
        }

        if (safariCache.mobEntities.isEmpty()) {
            safariCache.setActive(false);
            safariCache.setRewardAvailable(true);

            safariCache.hologram.updateHologram("reward");
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void rewardClaim(PlayerInteractEvent e) {
        if (!(e.getAction().equals(Action.RIGHT_CLICK_BLOCK))) return;
        if (e.getClickedBlock().getType() != Material.DRAGON_EGG) return;

        Player player = e.getPlayer();

        SafariCache safari = Safari.safariCache.stream().filter(obj -> obj.location.equals(e.getClickedBlock().getLocation())).findFirst().orElse(null);

        if (safari == null) return;

        if (safari.isRewardAvailable()) {
            if (safari.starter != null && !safari.starter.equals(player)) return;

            int amountOfRewards = safari.safariTierSave.getAmountOfRewards();
            boolean isChatWin = false;

            for (int i = 0; i < amountOfRewards; i++) {
                PriorityRewardSave prs = safari.safariTierSave.getRewards();

                if (EnchantUtil.getRandomDouble() < prs.getSpecialRewardChance()) {
                    SpecialRewardSave reward = Safari.getInstance().getRandomSpecialReward(safari.safariTierSave.getRewards().getSpecialRewards(), player.getItemInHand());

                    if (reward != null) {
                        isChatWin = reward.isBroadcast();

                        Safari.getInstance().addCommand(player.getUniqueId(), reward);

                        player.sendMessage(VelocityFeatures.chat(SafariConfig.getInstance().rewardMessage
                                .replace("<reward>", reward.getDisplayName())
                        ));

                        if (isChatWin) {
                            Bukkit.broadcastMessage(VelocityFeatures.chat(SafariConfig.getInstance().chatWinMessage
                                    .replace("<player>", player.getName())
                                    .replace("<reward>", reward.getDisplayName())
                            ));
                        }
                    }
                } else {
                    DropItemSave dropReward = Safari.getInstance().getRandomDrop(safari.getSafariTierSave().getRewards().getDropsId(), player.getItemInHand());
                    ItemMultiplierSave multiplier = dropReward.getMultiplier();

                    int amount = dropReward.getAmountToGive();

                    if (multiplier.isEnabled() && EnchantUtil.getRandomDouble() < multiplier.getChance()) {
                        amount += (int) EnchantUtil.getRandomDouble(1, multiplier.getAmount());
                    }

                    player.sendMessage(VelocityFeatures.chat(SafariConfig.getInstance().itemDropMessage
                            .replace("<amount>", amount + "")
                            .replace("<item>", dropReward.getDisplayName()
                    )));

                    Safari.getInstance().addItem(player.getUniqueId(), Safari.getInstance().getDropItem(dropReward, amount));
                }
            }

            if (!ItemUtil.isAirOrNull(player.getItemInHand())) {
                ItemStack sword = player.getItemInHand();

                NBTItem nbtItem = new NBTItem(sword);
                NBTCompound compound = nbtItem.getCompound("velocity_safari_sword");

                if (compound != null) {
                    Safari.getInstance().updateScore(player, nbtItem, isChatWin, false);
                    Safari.getInstance().updateLore(nbtItem, player);
                }

                player.setItemInHand(nbtItem.getItem());

            }

            safari.setActive(false);
            safari.setRewardAvailable(false);
            safari.setStarter(null);
            safari.setCooldown(System.currentTimeMillis() + safari.safariTierSave.getCooldown() * 1000L);
            safari.setResetTimer(60);

            safari.hologram.updateHologram("cooldown");
        }
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent e) {
        List<String> worlds = SafariConfig.getInstance().getWhitelistedWorlds();

        if (!worlds.contains(e.getTo().getWorld().getName())) return;
        if (worlds.contains(e.getFrom().getWorld().getName())) return;
        if (!Safari.getInstance().isInventoryEmpty(e.getPlayer())) {
            if (e.getPlayer().isOp()) return;

            e.getPlayer().sendMessage(VelocityFeatures.chat(SafariConfig.getInstance().inventoryEmpty));
            e.setCancelled(true);
            return;
        }

        Safari.getInstance().addPlayerGear(e.getPlayer());
    }

    @EventHandler
    public void teleportFromSafari(PlayerTeleportEvent e) {
        List<String> worlds = SafariConfig.getInstance().getWhitelistedWorlds();
        Player player = e.getPlayer();

        if (!worlds.contains(e.getFrom().getWorld().getName())) return;
        if (worlds.contains(e.getTo().getWorld().getName())) return;

        Safari.getInstance().updateInventory(player);
    }

    @EventHandler
    public void onSpawn(EntitySpawnEvent e) {
        if (e.getEntityType() == EntityType.SLIME) {
            Slime slime = (Slime) e.getEntity();
            if (slime.getSize() < 3) e.setCancelled(true);
        }

        if (e.getEntityType() == EntityType.MAGMA_CUBE) {
            MagmaCube magmaCube = (MagmaCube) e.getEntity();
            if (magmaCube.getSize() < 3) e.setCancelled(true);
        }
    }

    @EventHandler
    public void playerInteract(PlayerInteractEvent e) {
        if (!(e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) && !(e.getAction().equals(Action.LEFT_CLICK_BLOCK))) return;
        if (e.getClickedBlock().getType() != Material.DRAGON_EGG) return;
        e.setCancelled(true);
    }

    @EventHandler
    public void playerJoin(PlayerJoinEvent e) {
        List<String> worlds = SafariConfig.getInstance().getWhitelistedWorlds();
        Player player = e.getPlayer();

        if (!worlds.contains(player.getWorld().getName())) return;

        Safari.getInstance().updateInventory(player);

        player.teleport(Location.parseToLocation(SafariConfig.getInstance().leaveLocation));
    }

    @EventHandler
    public void disableArmorBreak(EntityDamageByEntityEvent e) {
        if (!(e.getEntity() instanceof Player)) return;

        Player player = (Player) e.getEntity();

        if (!SafariConfig.getInstance().getWhitelistedWorlds().contains(player.getWorld().getName())) return;

        ItemStack[] armor = player.getInventory().getArmorContents();

        for (ItemStack item : armor) {
            if (ItemUtil.isAirOrNull(item)) continue;

            if (item.getDurability() >= -1) item.setDurability(Short.MIN_VALUE);
        }
    }

    @EventHandler
    public void invClose(InventoryCloseEvent event) {
        String guiName = event.getInventory().getName();
        String configGuiName = VelocityFeatures.chat(SafariConfig.getInstance().getGuiName());

        if (!configGuiName.equalsIgnoreCase(guiName)) return;
        int money = 0;

        for (ItemStack content : event.getInventory().getContents()) {
            if (ItemUtil.hasNoItemMeta(content)) continue;

            NBTItem item = new NBTItem(content);
            NBTCompound compound = item.getCompound("velocity_safari_drop");

            money += compound.getInteger("sellPrice") * content.getAmount();
        }

        if (!(money > 0)) return;

        try {
            Essentials.getPlugin(Essentials.class).getUser(event.getPlayer()).giveMoney(BigDecimal.valueOf(money));
        } catch (MaxMoneyException err) {
            return;
        }
    }

    @EventHandler
    public void putItems(InventoryClickEvent event) {
        String guiName = event.getInventory().getName();
        String configGuiName = VelocityFeatures.chat(SafariConfig.getInstance().getGuiName());

        if (!guiName.equalsIgnoreCase(configGuiName)) return;
        if (ItemUtil.isAirOrNull(event.getCurrentItem())) return;

        NBTItem item = new NBTItem(event.getCurrentItem());
        NBTCompound compound = item.getCompound("velocity_safari_drop");

        if (compound == null) {
            event.setCancelled(true);
        }
    }

    @Getter
    private static SafariListener instance;

    public SafariListener() {
        instance = this;
    }

}
