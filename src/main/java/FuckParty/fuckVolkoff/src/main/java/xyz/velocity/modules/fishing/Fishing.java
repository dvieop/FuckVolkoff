package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.fishing;

import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTItem;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import lombok.Getter;
import net.minecraft.server.v1_8_R3.EntityFishingHook;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.velocity.VelocityFeatures;
import xyz.velocity.commands.CommandAPI;
import xyz.velocity.modules.fishing.commands.FishingCommand;
import xyz.velocity.modules.fishing.config.FishingConfig;
import xyz.velocity.modules.fishing.config.StatsConfig;
import xyz.velocity.modules.fishing.config.saves.*;
import xyz.velocity.modules.AbstractModule;
import xyz.velocity.modules.Module;
import xyz.velocity.modules.ModuleManager;
import xyz.velocity.modules.util.SkullUtil;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

@Module
public class Fishing extends AbstractModule {

    public Fishing() {
        instance = this;
    }

    @Getter
    private static Fishing instance;

    public void createItem(Player player) {
        RodItemSave rod = FishingConfig.getInstance().fishingRod;

        ItemStack item = new ItemStack(Material.getMaterial(rod.getMaterial()), 1);
        ItemMeta itemMeta = item.getItemMeta();

        itemMeta.spigot().setUnbreakable(true);
        item.setItemMeta(itemMeta);

        NBTItem nbtItem = new NBTItem(addGlow(item));

        NBTCompound nbtCompound = nbtItem.addCompound("velocity_fishing_rod");

        nbtCompound.setInteger("fishesCaught", 0);
        nbtCompound.setInteger("chatWins", 0);

        updateLore(nbtItem, player);

        player.getInventory().addItem(nbtItem.getItem());
    }

    public void updateScore(NBTItem nbtItem, boolean isChatWin) {
        NBTCompound compound = nbtItem.getCompound("velocity_fishing_rod");

        int fishesCaught = compound.getInteger("fishesCaught") + 1;
        int chatWins = compound.getInteger("chatWins");

        if (isChatWin) {
            chatWins += 1;
        }

        compound.setInteger("fishesCaught", fishesCaught);
        compound.setInteger("chatWins", chatWins);
    }

    public void updateLore(NBTItem nbtItem, Player player) {
        RodItemSave rod = FishingConfig.getInstance().fishingRod;

        ItemStack itemStack = nbtItem.getItem();
        ItemMeta itemMeta = itemStack.getItemMeta();

        NBTCompound compound = nbtItem.getCompound("velocity_fishing_rod");

        int fishesCaught = compound.getInteger("fishesCaught");
        int chatWins = compound.getInteger("chatWins");

        itemMeta.setDisplayName(VelocityFeatures.chat(rod.getDisplayName()));

        String lore = VelocityFeatures.chat(String.join("VDIB", rod.getLore())
                .replace("<owner>", player.getName())
                .replace("<fishes_caught>", fishesCaught + "")
                .replace("<chat_wins_caught>", chatWins + "")
        );

        itemMeta.setLore(Arrays.asList(lore.split("VDIB")));
        itemStack.setItemMeta(itemMeta);
    }

    public void giveFishItem(Player player, FishItemSave fish, int amount) {
        ItemStack item;

        if (fish.getMaterial().startsWith("head")) {
            item = createFishItem(fish, SkullUtil.skullItem(fish.getMaterial().replace("head-", "")), amount);
        } else {
            item = createFishItem(fish, amount);
        }

        NBTItem nbtItem = new NBTItem(item);

        NBTCompound nbtCompound = nbtItem.addCompound("velocity_fishing_fish");

        nbtCompound.setString("id", fish.getId());
        nbtCompound.setInteger("sellPrice", fish.getSellPrice());

        player.getInventory().addItem(nbtItem.getItem());
    }

    private ItemStack createFishItem(FishItemSave fish, int amount) {
        ItemStack item = new ItemStack(Material.getMaterial(fish.getMaterial()), amount, (byte) fish.getData());
        ItemMeta itemMeta = item.getItemMeta();

        itemMeta.setDisplayName(VelocityFeatures.chat(fish.getDisplayName()));

        String lore = VelocityFeatures.chat(String.join("VDIB", fish.getLore()));

        itemMeta.setLore(Arrays.asList(lore.split("VDIB")));

        //itemMeta.spigot().setUnbreakable(true);
        item.setItemMeta(itemMeta);

        if (fish.isGlow()) item = addGlow(item);

        return item;
    }

    private ItemStack createFishItem(FishItemSave fish, ItemStack item, int amount) {
        item.setAmount(amount);

        ItemMeta itemMeta = item.getItemMeta();

        itemMeta.setDisplayName(VelocityFeatures.chat(fish.getDisplayName()));

        String lore = VelocityFeatures.chat(String.join("VDIB", fish.getLore()));

        itemMeta.setLore(Arrays.asList(lore.split("VDIB")));

        //itemMeta.spigot().setUnbreakable(true);
        item.setItemMeta(itemMeta);

        if (fish.isGlow()) item = addGlow(item);

        return item;
    }
    public SpecialRewardSave getRandomSpecialReward(Player player) {
        int priority = getPriority(player);

        PriorityRewardSave prs = getPriorityReward(priority);

        if (prs == null) return null;

        List<SpecialRewardSave> rewardSaves = prs.getSpecialRewards();

        if (rewardSaves.isEmpty()) return null;

        double totalChances = 0.0;

        for (SpecialRewardSave reward : rewardSaves) {
            totalChances += reward.getChance();
        }

        int index = 0;

        for (double r = Math.random() * totalChances; index < rewardSaves.size() - 1; ++index) {
            r -= rewardSaves.get(index).getChance();
            if (r <= 0.0) break;
        }

        return rewardSaves.get(index);
    }

    public FishItemSave getRandomFish(Player player) {
        int priority = getPriority(player);

        PriorityRewardSave prs = getPriorityReward(priority);

        if (prs == null) return null;

        List<FishItemSave> fishSaves = getFishItems(prs.getFishesId());

        if (fishSaves.isEmpty()) return null;

        double totalChances = 0.0;

        for (FishItemSave fish : fishSaves) {
            totalChances += fish.getReelChance();
        }

        int index = 0;

        for (double r = Math.random() * totalChances; index < fishSaves.size() - 1; ++index) {
            r -= fishSaves.get(index).getReelChance();
            if (r <= 0.0) break;
        }

        return fishSaves.get(index);
    }

    public List<FishItemSave> getFishItems(List<String> fishIds) {
        List<FishItemSave> fishes = FishingConfig.getInstance().fishList;
        List<FishItemSave> newList = new ArrayList<>();

        for (String fishId : fishIds) {
            FishItemSave fish = fishes.stream().filter(obj -> obj.getId().equalsIgnoreCase(fishId)).findFirst().orElse(null);

            if (fish == null) continue;

            newList.add(fish);
        }

        return newList;
    }

    public int getPriority(Player player) {
        List<Priority> priorityList = FishingConfig.getInstance().rewardsPriority.getPriority();
        int priority = 0;

        for (Priority rewardPriority : priorityList) {
            if (player.hasPermission(rewardPriority.getPermission())) {
                if (priority > rewardPriority.getPriority()) continue;
                priority = rewardPriority.getPriority();
            }
        }

        return priority;
    }

    public int getReelTimeByPriority(int priority) {
        Priority p = FishingConfig.getInstance().rewardsPriority.getPriority().stream().filter(obj -> obj.getPriority() == priority).findFirst().orElse(null);

        if (p == null) return 100;

        return p.getReelTimeTicks();
    }

    public PriorityRewardSave getPriorityReward(int priority) {
        return FishingConfig.getInstance().rewards.stream().filter(obj -> obj.getPriority() == priority).findFirst().orElse(null);
    }

    public void updatePlayerData(Player player) {
        UUID id = player.getUniqueId();

        Object2ObjectMap<UUID, Integer> data = StatsConfig.getInstance().getData();

        if (!data.containsKey(id)) {
            data.put(id, 0);
        }

        int fishes = data.get(id).intValue() + 1;

        StatsConfig.getInstance().getData().put(id, fishes);
        //StatsConfig.getInstance().saveData();
    }

    public ItemStack addGlow(ItemStack item) {
        item.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
        ItemMeta meta = item.getItemMeta();
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        item.setItemMeta(meta);

        return item;
    }

    public void setBiteTime(FishHook hook, int time) {
        EntityFishingHook hookCopy = (EntityFishingHook) ((CraftEntity) hook).getHandle();

        Field fishCatchTime = null;

        try {
            fishCatchTime = EntityFishingHook.class.getDeclaredField("aw");
        } catch (NoSuchFieldException | SecurityException e) {
            e.printStackTrace();
        }

        fishCatchTime.setAccessible(true);

        try {
            fishCatchTime.setInt(hookCopy, time);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }

        fishCatchTime.setAccessible(false);
    }

    private void addPlaceholders() {
        ModuleManager.placeholders.put("fishing_caught", this);
    }

    @Override
    public String getName() {
        return "fishing";
    }

    @Override
    public boolean isEnabled() {
        return FishingConfig.getInstance().isEnabled();
    }

    @Override
    public void onEnable() {
        addPlaceholders();

        VelocityFeatures.registerEvent(new FishingListener());
        CommandAPI.getInstance().enableCommand(new FishingCommand());

        FishingConfig.getInstance().setEnabled(true);
    }

    @Override
    public void onDisable() {
        VelocityFeatures.unregisterEvent(FishingListener.getInstance());
        CommandAPI.getInstance().disableCommand(FishingCommand.class);

        FishingConfig.getInstance().setEnabled(false);
    }

    @Override
    public String placeholderRequest(Player player, String arg) {
        if (arg == "") return "";

        Object2ObjectMap<UUID, Integer> stats = StatsConfig.getInstance().getData();

        if (!stats.containsKey(player.getUniqueId())) return "0";

        switch (arg) {
            case "fishing_caught":
                return stats.get(player.getUniqueId()) + "";
        }

        return "";
    }

}
