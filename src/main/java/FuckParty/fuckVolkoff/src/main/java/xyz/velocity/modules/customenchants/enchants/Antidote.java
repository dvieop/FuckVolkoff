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
import xyz.velocity.modules.customenchants.EnumEnchantType;
import xyz.velocity.modules.customenchants.config.CustomEnchantConfig;
import xyz.velocity.modules.customenchants.config.saves.EnchantSave;
import xyz.velocity.modules.customenchants.CustomEnchants;
import xyz.velocity.modules.customenchants.EnchantManager;
import xyz.velocity.modules.customenchants.annotations.Enchant;
import xyz.velocity.modules.customenchants.events.EnchantProcEvent;
import xyz.velocity.modules.util.EnchantUtil;

import java.util.Arrays;

@Enchant
public class Antidote extends AbstractEnchant {

    private final CustomEnchantConfig config;

    public Antidote() {
        this.config = CustomEnchantConfig.getInstance();

        EnchantSave enchant = new EnchantSave(true, "Antidote", "&aAntidote", Arrays.asList("&7Chance to remove negative effects when getting hit!"), "LEGGINGS", "ANTIDOTE:<level>", 2, 5, 50, 1, false, this.extraInfo());

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
        return "Antidote";
    }

    @Override
    public boolean isVanillaEnchant() {
        return false;
    }

    @Override
    public String getEnchant() {
        return "ANTIDOTE";
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
        double getChance = getEnchantInformation(violated);

        if (chance < getChance) {
            EnchantProcEvent procEvent = CustomEnchants.getInstance().callEvent(violated, null, this);
            if (procEvent.isCancelled()) return;

            procEvent.activationMessage();

            PotionEffect potion = violated.getActivePotionEffects().stream().filter(obj -> obj.getType().equals(PotionEffectType.POISON)).findFirst().orElse(null);

            if (potion != null) {
                violated.removePotionEffect(potion.getType());
            }
        }
    }

    private JsonObject extraInfo() {
        JsonObject info = new JsonObject();

        info.addProperty("chancePerLevel", 5);

        info.toString();

        return info;
    }

    private double getEnchantInformation(Player player) {

        AtomicDouble chance = new AtomicDouble();

        Object2ObjectMap<EnchantSave, Integer> getEnchants = CustomEnchants.getInstance().getEnchantsFromPlayer(player);

        getEnchants.forEach((enchant, level) -> {
            if (!enchant.getName().equals(this.getName())) return;

            JsonObject obj = new Gson().fromJson(enchant.getExtra().get(0), JsonObject.class);

            chance.set(obj.get("chancePerLevel").getAsDouble() * level);
        });

        return chance.get();
    }

}
