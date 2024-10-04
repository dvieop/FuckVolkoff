package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.customenchants.enchants;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import xyz.velocity.modules.customenchants.EnumEnchantType;
import xyz.velocity.modules.customenchants.config.CustomEnchantConfig;
import xyz.velocity.modules.customenchants.config.saves.EnchantSave;
import xyz.velocity.modules.customenchants.CustomEnchants;
import xyz.velocity.modules.customenchants.EnchantManager;
import xyz.velocity.modules.customenchants.annotations.Enchant;
import xyz.velocity.modules.customenchants.events.EnchantProcEvent;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

@Enchant
public class Kraken extends AbstractEnchant {

    private final CustomEnchantConfig config;

    public Kraken() {
        this.config = CustomEnchantConfig.getInstance();

        EnchantSave enchant = new EnchantSave(true, "Kraken", "&cKraken", Arrays.asList("&7Deal more damage while in water"), "BOOTS", "KRAKEN:<level>", 3, 4, 50, 1, false, this.extraInfo());

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
        return "Kraken";
    }

    @Override
    public boolean isVanillaEnchant() {
        return false;
    }

    @Override
    public String getEnchant() {
        return "KRAKEN";
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

        Material blockType = damager.getLocation().getBlock().getType();

        if (blockType.equals(Material.WATER) || blockType.equals(Material.STATIONARY_WATER)) {
            EnchantProcEvent procEvent = CustomEnchants.getInstance().callEvent(damager, violated, this);
            if (procEvent.isCancelled()) return;

            procEvent.activationMessage();

            int damage = getEnchantInformation(damager);

            e.setDamage(e.getDamage() + damage);
            //violated.damage(e.getFinalDamage() + damage);
        }

    }

    private JsonObject extraInfo() {
        JsonObject info = new JsonObject();

        info.addProperty("damageIncreasePerLevel", 3);

        info.toString();

        return info;
    }

    private int getEnchantInformation(Player player) {

        AtomicInteger damage = new AtomicInteger();

        Object2ObjectMap<EnchantSave, Integer> getEnchants = CustomEnchants.getInstance().getEnchantsFromPlayer(player);

        getEnchants.forEach((enchant, level) -> {
            if (!enchant.getName().equals(this.getName())) return;

            JsonObject obj = new Gson().fromJson(enchant.getExtra().get(0), JsonObject.class);

            damage.set(obj.get("damageIncreasePerLevel").getAsInt() * level);
        });

        return damage.get();
    }

}
