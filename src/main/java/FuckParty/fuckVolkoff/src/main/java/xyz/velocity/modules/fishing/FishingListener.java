package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.fishing;

import com.earth2me.essentials.Essentials;
import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTItem;
import lombok.Getter;
import net.ess3.api.MaxMoneyException;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInventoryEvent;
import org.bukkit.inventory.ItemStack;
import xyz.velocity.VelocityFeatures;
import xyz.velocity.modules.fishing.config.FishingConfig;
import xyz.velocity.modules.fishing.config.saves.*;
import xyz.velocity.modules.util.EnchantUtil;
import xyz.velocity.modules.util.ItemUtil;

import java.math.BigDecimal;

public class FishingListener implements Listener {

    @EventHandler
    public void onReel(PlayerFishEvent e) {
        if (e.getState() == PlayerFishEvent.State.FISHING) {
            if (FishingConfig.getInstance().rewardsPriority.isEnabled()) {
                int priority = Fishing.getInstance().getPriority(e.getPlayer());
                int reelTime = Fishing.getInstance().getReelTimeByPriority(priority);

                Fishing.getInstance().setBiteTime(e.getHook(), reelTime);
            } else {
                Fishing.getInstance().setBiteTime(e.getHook(), FishingConfig.getInstance().reelTimeTicks);
            }
        }

        if (e.getState() == PlayerFishEvent.State.CAUGHT_FISH) {
            RodItemSave rodItemSave = FishingConfig.getInstance().fishingRod;

            Player player = e.getPlayer();
            ItemStack rod = player.getItemInHand();

            NBTItem nbtItem = new NBTItem(rod);
            NBTCompound compound = nbtItem.getCompound("velocity_fishing_rod");

            if (!rodItemSave.getWhitelistedWorlds().isEmpty() && !rodItemSave.getWhitelistedWorlds().contains(player.getWorld().getName())) return;
            if (compound == null) return;

            e.getCaught().remove();

            int priority = Fishing.getInstance().getPriority(player);

            PriorityRewardSave prs = Fishing.getInstance().getPriorityReward(priority);

            if (prs == null) return;

            double chance = EnchantUtil.getRandomDouble();

            SpecialRewardSave rewardSave = Fishing.getInstance().getRandomSpecialReward(player);

            Fishing.getInstance().updateScore(nbtItem, rewardSave.isBroadcast());
            Fishing.getInstance().updateLore(nbtItem, player);

            if (chance < prs.getSpecialRewardChance()) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), rewardSave.getCommand().replace("<player>", player.getName()));

                player.sendMessage(VelocityFeatures.chat(FishingConfig.getInstance().rewardMessage
                        .replace("<reward>", rewardSave.getChatName())
                ));

                if (rewardSave.isBroadcast()) {
                    Bukkit.broadcastMessage(VelocityFeatures.chat(FishingConfig.getInstance().chatWinMessage
                            .replace("<player>", player.getName())
                            .replace("<reward>", rewardSave.getChatName())
                    ));
                }
            } else {
                FishItemSave fishSave = Fishing.getInstance().getRandomFish(player);
                ItemMultiplierSave multiplier = fishSave.getMultiplier();

                int amount = fishSave.getAmountToGive();

                if (multiplier.isEnabled() && EnchantUtil.getRandomDouble() < multiplier.getChance()) {
                    amount += (int) EnchantUtil.getRandomDouble(1, multiplier.getAmount());
                }

                player.sendMessage(VelocityFeatures.chat(FishingConfig.getInstance().fishReelMessage
                        .replace("<amount>", amount + "")
                        .replace("<fish>", fishSave.getDisplayName())
                ));

                Fishing.getInstance().giveFishItem(player, fishSave, amount);
            }

            Fishing.getInstance().updatePlayerData(player);
            player.setItemInHand(nbtItem.getItem());
        }
    }

    @EventHandler
    public void invClose(InventoryCloseEvent event) {
        String guiName = event.getInventory().getName();
        String configGuiName = VelocityFeatures.chat(FishingConfig.getInstance().inventory.getGuiName());

        if (!configGuiName.equalsIgnoreCase(guiName)) return;
        int money = 0;

        for (ItemStack content : event.getInventory().getContents()) {
            if (ItemUtil.hasNoItemMeta(content)) continue;

            NBTItem item = new NBTItem(content);
            NBTCompound compound = item.getCompound("velocity_fishing_fish");

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
        String configGuiName = VelocityFeatures.chat(FishingConfig.getInstance().inventory.getGuiName());

        if (!guiName.equalsIgnoreCase(configGuiName)) return;
        if (ItemUtil.isAirOrNull(event.getCurrentItem())) return;

        NBTItem item = new NBTItem(event.getCurrentItem());
        NBTCompound compound = item.getCompound("velocity_fishing_fish");

        if (compound == null) {
            event.setCancelled(true);
        }
    }

    @Getter
    private static FishingListener instance;

    public FishingListener() {
        instance = this;
    }

}
