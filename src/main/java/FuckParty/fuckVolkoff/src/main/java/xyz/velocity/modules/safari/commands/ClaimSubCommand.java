package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.safari.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import xyz.velocity.VelocityFeatures;
import xyz.velocity.commands.ChildCommand;
import xyz.velocity.MainConfig;
import xyz.velocity.modules.safari.Safari;
import xyz.velocity.modules.safari.config.RewardConfig;
import xyz.velocity.modules.safari.config.SafariConfig;
import xyz.velocity.modules.safari.config.saves.SpecialRewardSave;

import java.util.*;

public class ClaimSubCommand extends ChildCommand {

    @Override
    protected boolean noChildrenExecute(CommandSender commandSender, String string, String[] args) {
        Player player = (Player) commandSender;

        if (SafariConfig.getInstance().getWhitelistedWorlds().contains(player.getWorld().getName())) {
            player.sendMessage(VelocityFeatures.chat(SafariConfig.getInstance().claimError));
            return false;
        }

        List<SpecialRewardSave> specialRewards = Safari.rewardCache.get(player.getUniqueId()).first;
        List<ItemStack> itemRewards = Safari.rewardCache.get(player.getUniqueId()).second;

        if ((specialRewards == null || specialRewards.isEmpty()) && (itemRewards == null || itemRewards.isEmpty())) {
            player.sendMessage(VelocityFeatures.chat(SafariConfig.getInstance().noRewards));
            return false;
        }

        giveSpecialReward(player, specialRewards, getEmptySlots(player));
        giveItemReward(player, itemRewards, getEmptySlots(player));

        List<SpecialRewardSave> specialList = Safari.rewardCache.get(player.getUniqueId()).first;
        List<ItemStack> itemList = Safari.rewardCache.get(player.getUniqueId()).second;

        if (!specialList.isEmpty() || !itemList.isEmpty()) {
            player.sendMessage(VelocityFeatures.chat(SafariConfig.getInstance().inventoryFull
                    .replace("<remaining>", specialList.size() + itemList.size() + "")
            ));
        }

        return false;
    }

    private void giveSpecialReward(Player player, List<SpecialRewardSave> list, int slots) {
        for (int i = 0; i < slots; i++) {
            if (list.isEmpty()) break;

            SpecialRewardSave rewardSave = list.get(0);

            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), rewardSave.getCommand().replace("<player>", player.getName()));

            list.remove(rewardSave);
        }

        int emptySlots = getEmptySlots(player);
        if (emptySlots > 0 && !list.isEmpty()) giveSpecialReward(player, Safari.rewardCache.get(player.getUniqueId()).first, emptySlots);
    }

    private void giveItemReward(Player player, List<ItemStack> list, int slots) {
        for (int i = 0; i < slots; i++) {
            if (list.isEmpty()) break;

            ItemStack itemStack = list.get(0);

            player.getInventory().addItem(itemStack);

            list.remove(itemStack);
        }

        int emptySlots = getEmptySlots(player);
        if (emptySlots > 0 && !list.isEmpty()) giveItemReward(player, Safari.rewardCache.get(player.getUniqueId()).second, emptySlots);
    }

    private int getEmptySlots(Player player) {
        int slots = 0;

        ItemStack[] items = player.getInventory().getContents();

        for (ItemStack item : items) {
            if (item == null)
                slots++;
        }

        return slots;
    }

    @Override
    public String getName() {
        return "claim";
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("claim");
    }

    @Override
    protected void sendUseMessage(CommandSender commandSender, String string, String[] args) {
        commandSender.sendMessage(VelocityFeatures.chat(MainConfig.getInstance().commandPrefix + "/safari claim"));
    }

}
