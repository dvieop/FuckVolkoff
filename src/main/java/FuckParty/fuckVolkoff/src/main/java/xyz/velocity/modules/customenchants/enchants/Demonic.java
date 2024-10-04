package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.customenchants.enchants;

import com.google.common.util.concurrent.AtomicDouble;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.velocity.VelocityFeatures;
import xyz.velocity.modules.customenchants.EnumEnchantType;
import xyz.velocity.modules.customenchants.config.CustomEnchantConfig;
import xyz.velocity.modules.customenchants.config.saves.EnchantSave;
import xyz.velocity.modules.customenchants.CustomEnchants;
import xyz.velocity.modules.customenchants.EnchantManager;
import xyz.velocity.modules.customenchants.annotations.Enchant;
import xyz.velocity.modules.customenchants.events.EnchantProcEvent;
import xyz.velocity.modules.util.EnchantUtil;
import xyz.velocity.modules.util.Pair;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

@Enchant
public class Demonic extends AbstractEnchant {

    private final CustomEnchantConfig config;

    public Demonic() {
        this.config = CustomEnchantConfig.getInstance();

        EnchantSave enchant = new EnchantSave(true, "Demonic", "&cDemonic", Arrays.asList("&7Chance to get Strength III on hit!"), "CHESTPLATE", "DEMONIC:<level>", 3, 2, 50, 1, false, this.extraInfo());

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
        return "Demonic";
    }

    @Override
    public boolean isVanillaEnchant() {
        return false;
    }

    @Override
    public String getEnchant() {
        return "DEMONIC";
    }

    @Override
    public EnumEnchantType getEnchantType() {
        return EnumEnchantType.PVP;
    }

    @Override
    public <T extends Event> void runTask(T event) {
        EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event;

        Player damager = (Player) e.getDamager();

        double chance = EnchantUtil.getRandomDouble();

        Pair<Double, Integer> getInformation = getEnchantInformation(damager);

        if (getInformation == null) return;
        if (chance < getInformation.first) {
            EnchantProcEvent procEvent = CustomEnchants.getInstance().callEvent(damager, e.getEntity(), this);
            if (procEvent.isCancelled()) return;

            procEvent.activationMessage();

            if (damager.hasPotionEffect(PotionEffectType.INCREASE_DAMAGE)) {
                damager.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
            }

            damager.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, getInformation.second * 20, 2));

            Object2ObjectMap.Entry<EnchantSave, Integer> hasStrength = CustomEnchants.getInstance().getEnchantByEffect(damager, "INCREASE_DAMAGE");

            if (hasStrength != null) {

                BukkitRunnable bukkitRunnable = new BukkitRunnable() {

                    @Override
                    public void run() {
                        damager.addPotionEffect(EnchantUtil.getEffect(hasStrength.getKey().getEnchant().replace("<level>", hasStrength.getValue() - 1 + "")));
                    }

                };

                bukkitRunnable.runTaskLater(VelocityFeatures.getInstance(), getInformation.second * 20 + 1);

            }
        }

    }

    private JsonObject extraInfo() {
        JsonObject info = new JsonObject();

        info.addProperty("chancePerLevel", 5);
        info.addProperty("durationPerLevel", 3);

        info.toString();

        return info;
    }

    private Pair<Double, Integer> getEnchantInformation(Player player) {

        AtomicDouble chance = new AtomicDouble();
        AtomicInteger duration = new AtomicInteger();

        Object2ObjectMap<EnchantSave, Integer> getEnchants = CustomEnchants.getInstance().getEnchantsFromPlayer(player);

        getEnchants.forEach((enchant, level) -> {
            if (!enchant.getName().equals(this.getName())) return;

            JsonObject obj = new Gson().fromJson(enchant.getExtra().get(0), JsonObject.class);

            chance.set(obj.get("chancePerLevel").getAsDouble() * level);
            duration.set(obj.get("durationPerLevel").getAsInt() * level);
        });

        return new Pair<>(chance.get(), duration.get());
    }

}
