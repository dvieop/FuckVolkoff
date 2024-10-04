package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.customenchants.enchants;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDeathEvent;
import xyz.velocity.modules.customenchants.EnumEnchantType;
import xyz.velocity.modules.customenchants.config.CustomEnchantConfig;
import xyz.velocity.modules.customenchants.config.saves.EnchantSave;
import xyz.velocity.modules.customenchants.config.saves.RewardSave;
import xyz.velocity.modules.customenchants.CustomEnchants;
import xyz.velocity.modules.customenchants.EnchantManager;
import xyz.velocity.modules.customenchants.annotations.Enchant;
import xyz.velocity.modules.customenchants.events.EnchantProcEvent;
import xyz.velocity.modules.util.EnchantUtil;
import xyz.velocity.modules.util.Pair;

import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Enchant
public class KeyFinder extends AbstractEnchant {

    private final CustomEnchantConfig config;

    public KeyFinder() {
        this.config = CustomEnchantConfig.getInstance();

        EnchantSave enchant = new EnchantSave(true, "Keyfinder", "&cKey Finder", Arrays.asList("&7Gain rewards from killing mobs!"), "SWORD", "KEYFINDER:<level>", 3, 3, 50, 1, false, this.extraInfo());

        if (!config.getEnchantList().stream().anyMatch(obj -> obj.getName().equals(enchant.getName()))) {
            config.getEnchantList().add(enchant);
        }

        EnchantManager.nonVanillaEnchants.put(this.getName(), this);
    }

    @Override
    public boolean isEnabled() {
        return CustomEnchantConfig.getInstance().getEnchantList().stream().filter(obj -> obj.getName().equals(this.getName())).findFirst().get().isEnabled();
    }

    @Override
    public String getName() {
        return "Keyfinder";
    }

    @Override
    public boolean isVanillaEnchant() {
        return false;
    }

    @Override
    public String getEnchant() {
        return "KEYFINDER";
    }

    @Override
    public EnumEnchantType getEnchantType() {
        return EnumEnchantType.GRINDING;
    }

    @Override
    public <T extends Event> void runTask(T event) {
        EntityDeathEvent e;

        try {
            e = (EntityDeathEvent) event;
        } catch (Throwable err) {
            return;
        }

        Player p = e.getEntity().getKiller();

        Pair<Double, List<RewardSave>> enchantInfo = getEnchantInformation(p);

        if (enchantInfo.first == null || enchantInfo.second == null) return;

        double chance = EnchantUtil.getRandomDouble();

        if (chance < enchantInfo.first) {
            EnchantProcEvent procEvent = CustomEnchants.getInstance().callEvent(p, null, this);
            if (procEvent.isCancelled()) return;

            procEvent.activationMessage();

            List<RewardSave> rewards = enchantInfo.second;

            double totalChance = 0.0;

            for (RewardSave s : rewards) {
                totalChance += s.getChance();
            }

            int rewardIndex = 0;

            for (double r = Math.random() * totalChance; rewardIndex < rewards.size() - 1; ++rewardIndex) {
                r -= rewards.get(rewardIndex).getChance();
                if (r <= 0.0) break;
            }

            RewardSave toGive = rewards.get(rewardIndex);

            if (toGive == null) return;

            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), toGive.getCommand().replace("<player>", p.getName()));
        }
    }

    private JsonObject extraInfo() {
        JsonObject info = new JsonObject();

        List<RewardSave> rewards = new ArrayList<RewardSave>(Collections.singletonList(new RewardSave("ex", 10)));

        JsonArray result = (JsonArray) new Gson().toJsonTree(rewards,
                new TypeToken<List<RewardSave>>() {
                }.getType());

        info.addProperty("chancePerLevel", 7);
        info.add("rewards", result);

        info.toString();

        return info;
    }

    private Pair<Double, List<RewardSave>> getEnchantInformation(Player player) {

        double chance = 0;
        List<RewardSave> rewards = new ArrayList<>();

        Map<EnchantSave, Integer> getEnchants = CustomEnchants.getInstance().getEnchantsFromPlayer(player);

        for (Map.Entry<EnchantSave, Integer> entry : getEnchants.entrySet()) {
            EnchantSave enchant = entry.getKey();
            int level = entry.getValue();

            if (!enchant.getName().equals(this.getName())) continue;

            JsonObject obj = new Gson().fromJson(enchant.getExtra().get(0), JsonObject.class);

            chance = obj.get("chancePerLevel").getAsDouble() * level;

            Type listType = new TypeToken<List<RewardSave>>() {}.getType();
            rewards = new Gson().fromJson(obj.get("rewards") , listType);
        }

        return new Pair<>(chance, rewards);
    }

}
