package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.stronghold.listeners;

import com.golfing8.kore.FactionsKore;
import com.golfing8.kore.event.StackedEntityDeathEvent;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import xyz.velocity.VelocityFeatures;
import xyz.velocity.modules.stronghold.config.DataConfig;
import xyz.velocity.modules.stronghold.config.StrongholdConfig;
import xyz.velocity.modules.stronghold.config.saves.RewardSave;
import xyz.velocity.modules.util.Block;
import xyz.velocity.modules.util.CapturePoint;
import xyz.velocity.modules.stronghold.Stronghold;
import xyz.velocity.modules.stronghold.util.MobWrapper;
import xyz.velocity.modules.util.ItemUtil;
import xyz.velocity.modules.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class StrongholdListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void blockBreak(BlockBreakEvent e) {
        Pair<String, Block> blockData = Stronghold.getStrongholdBlockData(e.getBlock());

        if (blockData.first == null || blockData.second == null) return;

        String getFaction = FactionsKore.getIntegration().getPlayerFactionId(e.getPlayer());
        String stronghold = blockData.first;
        String factionOwning = DataConfig.getInstance().strongholds.get(stronghold).getFaction();

        if (getFaction.equals(factionOwning)) {
            e.getBlock().setType(Material.AIR);
        } else {
            Block block = blockData.second;
            block.setUses(block.getUses() - 1);

            e.getPlayer().sendMessage(VelocityFeatures.chat(StrongholdConfig.getInstance().getWallMined()
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
            Pair<String, Block> blockData = Stronghold.getStrongholdBlockData(e.getClickedBlock());

            if (blockData.first == null || blockData.second == null) return;

            String getFaction = FactionsKore.getIntegration().getPlayerFactionId(e.getPlayer());
            String stronghold = blockData.first;
            String factionOwning = DataConfig.getInstance().strongholds.get(stronghold).getFaction();

            if (getFaction.equals(factionOwning)) {
                e.getClickedBlock().setType(Material.AIR);
            } else {
                Block block = blockData.second;
                block.setUses(block.getUses() - 1);

                e.getPlayer().sendMessage(VelocityFeatures.chat(StrongholdConfig.getInstance().getWallMined()
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
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDeath(EntityDeathEvent e) {
        if (!Stronghold.getInstance().spawnedMobs.containsKey(e.getEntity().getUniqueId())) return;

        e.getDrops().clear();

        Player player = e.getEntity().getKiller();

        MobWrapper mobWrapper = Stronghold.getInstance().spawnedMobs.get(e.getEntity().getUniqueId());

        String stronghold = mobWrapper.getStronghold();
        String faction;

        try {
            faction = FactionsKore.getIntegration().getPlayerFactionId(player);
        } catch (Throwable err) {
            return;
        }

        String factionControlling = DataConfig.getInstance().getStrongholds().get(stronghold).getFaction();

        CapturePoint capturePoint = Stronghold.getInstance().capturePoints.stream().filter(obj -> obj.getStronghold().getName().equals(stronghold)).findFirst().get();

        if (factionControlling.equals(faction) && capturePoint != null) {
            List<RewardSave> rewards = mobWrapper.getMobSave().getRewards();

            double totalChances = 0.0;

            for (RewardSave reward : rewards) {
                totalChances += reward.getChance();
            }

            int index = 0;

            for (double r = Math.random() * totalChances; index < rewards.size() - 1; ++index) {
                r -= rewards.get(index).getChance();
                if (r <= 0.0) break;
            }

            RewardSave reward = rewards.get(index);

            if (reward != null) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), reward.getCommand().replace("<player>", player.getName()));
                player.sendMessage(VelocityFeatures.chat(StrongholdConfig.getInstance().getMobKillMessage()
                        .replace("<reward>", reward.getDisplayName())
                ));

                if (reward.isBroadcast()) {
                    Bukkit.broadcastMessage(VelocityFeatures.chat(StrongholdConfig.getInstance().broadcastReward
                            .replace("<player>", player.getName())
                            .replace("<reward>", reward.getDisplayName())
                            .replace("<stronghold>", capturePoint.getStronghold().getChatName())
                    ));
                }
            }
        }

        Stronghold.getInstance().spawnedMobs.remove(e.getEntity().getUniqueId());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onDamage(EntityDamageByEntityEvent e) {
        if (!Stronghold.getInstance().spawnedMobs.containsKey(e.getDamager().getUniqueId())) return;

        int damage = Stronghold.getInstance().spawnedMobs.get(e.getDamager().getUniqueId()).getMobSave().getDamage();

        e.setDamage(e.getFinalDamage() + damage);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onTeleport(PlayerTeleportEvent e) {
        if (StrongholdConfig.getInstance().getEffectsDisabledWorlds().contains(e.getTo().getWorld().getName())) {
            Stronghold.getInstance().removeEffects(e.getPlayer());
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onClick(InventoryClickEvent e) {
        if (!e.getInventory().getName().equals(VelocityFeatures.chat(StrongholdConfig.getInstance().guiName))) return;

        e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void tntDrops(StackedEntityDeathEvent e) {
        String factionID = FactionsKore.getIntegration().getFactionsIdAt(e.getStackedEntity().getLocation());

        for (CapturePoint capturePoint : Stronghold.getInstance().capturePoints) {
            if (!factionID.equals(capturePoint.getFactionOwning())) continue;

            for (String customEffect : capturePoint.getStronghold().getCustomEffects()) {
                String[] split = customEffect.split(":");

                if (!split[0].equalsIgnoreCase("tntboost")) continue;

                double boost = Double.parseDouble(split[1]);
                List<ItemStack> drops = new ArrayList<>();

                for(ItemStack itemStack : e.getStackedDrops().getDrops()) {
                    if (itemStack.getType() == Material.TNT) {
                        int amt = (int) (itemStack.getAmount() * boost);

                        itemStack.setAmount(amt);

                        drops.add(itemStack);
                    } else {
                        drops.add(itemStack);
                    }
                }

                e.getStackedDrops().setDrops(drops);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onMobDamage(EntityDamageByEntityEvent e) {
        if (!Stronghold.getInstance().spawnedMobs.containsKey(e.getEntity().getUniqueId())) return;

        MobWrapper mobWrapper = Stronghold.getInstance().spawnedMobs.get(e.getEntity().getUniqueId());

        double health = Stronghold.getInstance().roundAvoid(((LivingEntity) e.getEntity()).getHealth(), 0);

        e.getEntity().setCustomName(VelocityFeatures.chat(mobWrapper.getMobSave().getDisplayName()
                .replace("<level>", mobWrapper.getLevel() + "")
                .replace("<health>", health + "")
        ));
    }

    @Getter
    private static StrongholdListener instance;

    public StrongholdListener() {
        instance = this;
    }

}
