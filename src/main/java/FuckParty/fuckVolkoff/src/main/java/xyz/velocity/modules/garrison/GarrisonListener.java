package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.garrison;

import com.golfing8.kore.FactionsKore;
import com.golfing8.kore.event.KothCaptureEvent;
import eu.decentsoftware.holograms.event.HologramClickEvent;
import lombok.Getter;
import me.badbones69.crazyenvoys.api.events.OpenEnvoyEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.potion.PotionEffect;
import xyz.velocity.VelocityFeatures;
import xyz.velocity.modules.garrison.config.GarrisonConfig;
import xyz.velocity.modules.garrison.config.saves.DeathBanSave;
import xyz.velocity.modules.safari.Safari;
import xyz.velocity.modules.util.*;

import java.util.Objects;

public class GarrisonListener implements Listener {

    /*@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void blockBreak(BlockBreakEvent e) {
        Pair<String, Block> blockData = Garrison.getGarrisonBlockData(e.getBlock());

        if (blockData.first == null || blockData.second == null) return;

        String getFaction = FactionsKore.getIntegration().getPlayerFactionId(e.getPlayer());
        String garrison = blockData.first;
        String factionOwning = DataConfig.getInstance().garrison.get(garrison).getFaction();

        if (getFaction.equals(factionOwning)) {
            e.getBlock().setType(Material.AIR);
        } else {
            Block block = blockData.second;
            block.setUses(block.getUses() - 1);

            e.getPlayer().sendMessage(VelocityFeatures.chat(GarrisonConfig.getInstance().getWallMined()
                    .replace("<amount>", block.getUses() + "")
            ));

            if (block.getUses() <= 0) {
                e.getBlock().setType(Material.AIR);
                e.setCancelled(true);
                return;
            }

            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onInteract(PlayerInteractEvent e) {
        if (!(e.getAction().equals(Action.LEFT_CLICK_BLOCK))) return;
        if (ItemUtil.isAirOrNull(e.getPlayer().getItemInHand())) return;
        if (!e.getPlayer().getItemInHand().getType().name().endsWith("_PICKAXE")) return;

        ItemStack pickaxe = e.getPlayer().getItemInHand();

        if (!pickaxe.getEnchantments().keySet().contains(Enchantment.DIG_SPEED)) return;

        int speed = pickaxe.getEnchantments().get(Enchantment.DIG_SPEED);

        if (speed > 5) {
            Pair<String, Block> blockData = Garrison.getGarrisonBlockData(e.getClickedBlock());

            if (blockData.first == null || blockData.second == null) return;

            String getFaction = FactionsKore.getIntegration().getPlayerFactionId(e.getPlayer());
            String garrison = blockData.first;
            String factionOwning = DataConfig.getInstance().garrison.get(garrison).getFaction();

            if (getFaction.equals(factionOwning)) {
                e.getClickedBlock().setType(Material.AIR);
            } else {
                Block block = blockData.second;
                block.setUses(block.getUses() - 1);

                e.getPlayer().sendMessage(VelocityFeatures.chat(GarrisonConfig.getInstance().getWallMined()
                        .replace("<amount>", block.getUses() + "")
                ));

                if (block.getUses() <= 0) {
                    e.getClickedBlock().setType(Material.AIR);
                    e.setCancelled(true);
                    return;
                }

                e.setCancelled(true);
            }
        }
    }*/

    @EventHandler
    public void expEvent(PlayerExpChangeEvent e) {
        Player player = e.getPlayer();
        String faction = FactionsKore.getIntegration().getPlayerFactionId(player);

        BoostCache boostCache = Garrison.getInstance().getCurrentBoost();

        if (!Garrison.getInstance().isEligible(faction, EnumBoostMode.CROP)) return;

        e.setAmount((int) (e.getAmount() * boostCache.getMultiplier()));

    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void cropGrowth(BlockGrowEvent e) {
        /*if (e.isCancelled()) return;

        String faction = FactionsKore.getIntegration().getFactionsIdAt(e.getBlock().getLocation());

        BoostCache boostCache = Garrison.getInstance().getCurrentBoost();

        if (!Garrison.getInstance().isEligible(faction, EnumBoostMode.CROP)) return;

        e.getBlock().setData((byte) (e.getBlock().getData() + (boostCache.getMultiplier() - 1)));*/
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDamage(EntityDamageByEntityEvent e) {
        /*BoostCache boostCache = Garrison.getInstance().getCurrentBoost();

        if (e.getDamager() instanceof Player) {
            String faction = FactionsKore.getIntegration().getPlayerFactionId((Player) e.getDamager());

            if (!Garrison.getInstance().isEligible(faction, EnumBoostMode.DAMAGE)) return;

            e.setDamage(e.getDamage() * boostCache.getMultiplier());
        }

        if (e.getEntity() instanceof Player) {
            String faction = FactionsKore.getIntegration().getPlayerFactionId((Player) e.getEntity());

            if (!Garrison.getInstance().isEligible(faction, EnumBoostMode.REDUCE)) return;

            e.setDamage(e.getDamage() * boostCache.getMultiplier());
        }*/
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void safariMobDeath(EntityDeathEvent e) {
        if (!Safari.getInstance().isEnabled()) return;
        if (!Safari.mobCache.containsKey(e.getEntity().getUniqueId())) return;
        if (e.getEntity().getKiller() == null) return;

        Player player = e.getEntity().getKiller();

        Garrison.getInstance().handleXp(player, "safari");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void regularMobDeath(EntityDeathEvent e) {
        if (e.getEntity().getKiller() == null) return;

        Player player = e.getEntity().getKiller();

        Garrison.getInstance().handleXp(player, "mob");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void playerKill(PlayerDeathEvent e) {
        if (e.getEntity().getKiller() == null) return;
        if (Objects.equals(Garrison.getInstance().capturePoint.getLocation1().getWorld().getName(), e.getEntity().getLocation().getWorld().getName())) {
            DeathBanSave dbs = GarrisonConfig.getInstance().getGarrison().getDeathBan();

            if (dbs.isEnabled()) {
                Garrison.deathBanMap.put(e.getEntity().getUniqueId(), System.currentTimeMillis() + (dbs.getDuration() * 60 * 1000));
            }
        }

        Player player = e.getEntity().getKiller();

        Garrison.getInstance().handleXp(player, "player");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void fishing(PlayerFishEvent e) {
        if (e.isCancelled()) return;
        if (!(e.getState() == PlayerFishEvent.State.CAUGHT_FISH)) return;

        Garrison.getInstance().handleXp(e.getPlayer(), "fish");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void kothCapture(KothCaptureEvent e) {
        Garrison.getInstance().handleXp(e.getPlayerCapper(), "koth");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void kothCapture(OpenEnvoyEvent e) {
        if (e.isCancelled()) return;

        Garrison.getInstance().handleXp(e.getPlayer(), "envoy");
    }

    @EventHandler
    public void holoClick(HologramClickEvent e) {
        if (e.getHologram().getName() != "Garrison") return;
        if (!Garrison.getInstance().isFactionOwning(Garrison.getInstance().capturePoint, e.getPlayer())) return;

        //Garrison.getInstance().mode = Garrison.getInstance().mode.next();
        Garrison.getInstance().setNonDisabledBoosts();
        Garrison.getInstance().hologram.updateLines();
    }
    @EventHandler
    public void onTeleport(PlayerTeleportEvent e) {
        Player p = e.getPlayer();
        CapturePoint capturePoint = Garrison.getInstance().capturePoint;

        if (e.getCause().equals(PlayerTeleportEvent.TeleportCause.ENDER_PEARL)) return;
        if (!capturePoint.getLocation1().getWorld().getName().equals(e.getTo().getWorld().getName())) return;
        if (Garrison.getInstance().isOnGrace(capturePoint) && !Garrison.getInstance().isFactionOwning(capturePoint, p) && !p.isOp()) {
            e.setCancelled(true);
            return;
        }
        if (GarrisonConfig.getInstance().getGarrison().getDeathBan().isEnabled()) {
            if (Garrison.getInstance().isDeathBanned(p)) {
                p.sendMessage(
                        VelocityFeatures.chat(GarrisonConfig.getInstance().getGarrison().getDeathBan().getWarpMessage()
                                .replace("<duration>", Garrison.getInstance().formatTime(Garrison.getInstance().longToSeconds(p)))
                        )
                );
                e.setCancelled(true);
                return;
            }
        }
        if (!InventoryUtil.isInventoryEmpty(p)) {
            if (p.isOp()) return;

            p.sendMessage(VelocityFeatures.chat(GarrisonConfig.getInstance().inventoryEmpty));
            e.setCancelled(true);
            return;
        }

        Garrison.getInstance().addPlayerGear(e.getPlayer());
    }

    @EventHandler
    public void teleportFromGarrison(PlayerTeleportEvent e) {
        CapturePoint capturePoint = Garrison.getInstance().capturePoint;
        String mainWorld = capturePoint.getLocation1().getWorld().getName();

        if (e.getPlayer().isOp()) return;
        if (e.getFrom().getWorld().getName().equals(mainWorld) && !e.getTo().getWorld().getName().equals(mainWorld)) {
            Player player = e.getPlayer();

            player.getInventory().clear();

            player.getInventory().setHelmet(null);
            player.getInventory().setChestplate(null);
            player.getInventory().setLeggings(null);
            player.getInventory().setBoots(null);

            player.updateInventory();

            for (PotionEffect activePotionEffect : player.getActivePotionEffects()) {
                player.removePotionEffect(activePotionEffect.getType());
            }
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        CapturePoint capturePoint = Garrison.getInstance().capturePoint;
        String mainWorld = capturePoint.getLocation1().getWorld().getName();

        if (p.getLocation().getWorld().getName().equals(mainWorld)) {
            if (!Garrison.getInstance().isFactionOwning(capturePoint, p)) {
                p.teleport(Location.parseToLocation(GarrisonConfig.getInstance().leaveLocation));
            }
        }
    }

    public GarrisonListener() {
        instance = this;
    }

    @Getter
    private static GarrisonListener instance;
}
