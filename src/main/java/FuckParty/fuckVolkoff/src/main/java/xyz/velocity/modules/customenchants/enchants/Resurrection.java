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
public class Resurrection extends AbstractEnchant {

    private final CustomEnchantConfig config;

    public Resurrection() {
        this.config = CustomEnchantConfig.getInstance();

        EnchantSave enchant = new EnchantSave(true, "Resurrection", "&cResurrection", Arrays.asList("&7Chance of gaining regeneration 3 and speed 4 when low"), "BOOTS", "RESURRECTION:<level>", 3, 4, 50, 1, false, this.extraInfo());

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
        return "Resurrection";
    }

    @Override
    public boolean isVanillaEnchant() {
        return false;
    }

    @Override
    public String getEnchant() {
        return "RESURRECTION";
    }

    @Override
    public EnumEnchantType getEnchantType() {
        return EnumEnchantType.PVP;
    }

    @Override
    public <T extends Event> void runTask(T event) {


        EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event;

        Player violated = (Player) e.getEntity();

        double chance = EnchantUtil.getRandomDouble();

        Pair<Double, Integer> getInformation = getEnchantInformation(violated);

        if (getInformation == null) return;
        if (chance < getInformation.first && violated.getHealth() < 6) {
            EnchantProcEvent procEvent = CustomEnchants.getInstance().callEvent(violated, null, this);
            if (procEvent.isCancelled()) return;

            procEvent.activationMessage();

            if (violated.hasPotionEffect(PotionEffectType.SPEED)) {
                violated.removePotionEffect(PotionEffectType.SPEED);
            }

            violated.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, getInformation.second * 20, 3));
            violated.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, getInformation.second * 20, 2));

            Object2ObjectMap.Entry<EnchantSave, Integer> hasSpeed = CustomEnchants.getInstance().getEnchantByEffect(violated, "SPEED");

            if (hasSpeed != null) {

                BukkitRunnable bukkitRunnable = new BukkitRunnable() {

                    @Override
                    public void run() {
                        violated.addPotionEffect(EnchantUtil.getEffect(hasSpeed.getKey().getEnchant().replace("<level>", hasSpeed.getValue() - 1 + "")));
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
