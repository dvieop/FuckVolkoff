package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.customenchants.enchants;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
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
public class Deathbringer extends AbstractEnchant {

    private final CustomEnchantConfig config;

    public Deathbringer() {
        this.config = CustomEnchantConfig.getInstance();

        EnchantSave enchant = new EnchantSave(true, "Deathbringer", "&cDeathbringer", Arrays.asList("&7Deal more damage to your enemies!"), "LEGGINGS", "DEATHBRINGER:<level>", 3, 3, 20, 1, false, this.extraInfo());

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
        return "Deathbringer";
    }

    @Override
    public boolean isVanillaEnchant() {
        return false;
    }

    @Override
    public String getEnchant() {
        return "DEATHBRINGER";
    }

    @Override
    public EnumEnchantType getEnchantType() {
        return EnumEnchantType.PVP;
    }

    @Override
    public <T extends Event> void runTask(T event) {
        EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event;

        Player damager = (Player) e.getDamager();

        EnchantProcEvent procEvent = CustomEnchants.getInstance().callEvent(damager, e.getEntity(), this);
        if (procEvent.isCancelled()) return;

        procEvent.activationMessage();

        int damage = getEnchantInformation(damager);

        if (damage == 0) return;

        e.setDamage(e.getDamage() + damage);
    }

    private JsonObject extraInfo() {
        JsonObject info = new JsonObject();

        info.addProperty("damagePerLevel", 2);

        info.toString();

        return info;
    }

    private int getEnchantInformation(Player player) {
        AtomicInteger damage = new AtomicInteger(0);

        Object2ObjectMap<EnchantSave, Integer> getEnchants = CustomEnchants.getInstance().getEnchantsFromPlayer(player);

        getEnchants.forEach((enchant, level) -> {
            if (!enchant.getName().equals(this.getName())) return;

            JsonObject obj = new Gson().fromJson(enchant.getExtra().get(0), JsonObject.class);

            damage.set(obj.get("damagePerLevel").getAsInt() * level);
        });

        return damage.get();
    }

}
