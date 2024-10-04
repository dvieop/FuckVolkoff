package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.customenchants.enchants;

import com.google.common.util.concurrent.AtomicDouble;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
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
import xyz.velocity.modules.util.Pair;

import java.util.Arrays;

@Enchant
public class Blind extends AbstractEnchant {

    private final CustomEnchantConfig config;

    public Blind() {
        this.config = CustomEnchantConfig.getInstance();

        EnchantSave enchant = new EnchantSave(true, "Blind", "&aBlind", Arrays.asList("&7Give weakness when getting hit"), "HELMET", "BLIND:<level>", 2, 5, 20, 1, false, this.extraInfo());

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
        return "Blind";
    }

    @Override
    public boolean isVanillaEnchant() {
        return false;
    }

    @Override
    public String getEnchant() {
        return "BLIND";
    }

    @Override
    public EnumEnchantType getEnchantType() {
        return EnumEnchantType.PVP;
    }

    @Override
    public <T extends Event> void runTask(T event) {
        EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event;

        Player damager = (Player) e.getDamager();
        Player violated = (Player) e.getEntity();

        double chance = EnchantUtil.getRandomDouble();

        Pair<Double, Double> enchantInformation = getEnchantInformation(damager);

        if (enchantInformation == null) return;
        if (chance < enchantInformation.second) {
            EnchantProcEvent procEvent = CustomEnchants.getInstance().callEvent(damager, violated, this);
            if (procEvent.isCancelled()) return;

            procEvent.activationMessage();

            double duration = enchantInformation.first;
            violated.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, (int) duration * 20, 1));
        }
    }

    private JsonObject extraInfo() {
        JsonObject info = new JsonObject();

        info.addProperty("durationPerLevel", 1);
        info.addProperty("chancePerLevel", 5);

        info.toString();

        return info;
    }

    private Pair<Double, Double> getEnchantInformation(Player player) {
        EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();

        AtomicDouble duration = new AtomicDouble(0);
        AtomicDouble chance = new AtomicDouble(0);

        Object2ObjectMap<EnchantSave, Integer> toReturn = CustomEnchants.getInstance().getEnchantsFromPlayer(player);

        toReturn.forEach((enchant, level) -> {
            if (!enchant.getName().equals(this.getName())) return;
            JsonObject obj = new Gson().fromJson(enchant.getExtra().get(0), JsonObject.class);

            duration.set(obj.get("durationPerLevel").getAsDouble() * level);
            chance.set(obj.get("chancePerLevel").getAsDouble() * level);
        });

        return new Pair<>(duration.get(), chance.get());
    }

}
