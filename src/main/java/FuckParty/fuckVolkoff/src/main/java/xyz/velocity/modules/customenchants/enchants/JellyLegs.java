package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.customenchants.enchants;

import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageEvent;
import xyz.velocity.modules.customenchants.EnumEnchantType;
import xyz.velocity.modules.customenchants.config.CustomEnchantConfig;
import xyz.velocity.modules.customenchants.config.saves.EnchantSave;
import xyz.velocity.modules.customenchants.CustomEnchants;
import xyz.velocity.modules.customenchants.EnchantManager;
import xyz.velocity.modules.customenchants.annotations.Enchant;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

@Enchant
public class JellyLegs extends AbstractEnchant {

    private final CustomEnchantConfig config;

    public JellyLegs() {
        this.config = CustomEnchantConfig.getInstance();

        EnchantSave enchant = new EnchantSave(true, "JellyLegs", "&cJelly Legs", Arrays.asList("&7Ignore fall damage!"), "BOOTS", "JELLY_LEGS:<level>", 3, 1, 50, 1, false, new JsonObject());

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
        return "JellyLegs";
    }

    @Override
    public boolean isVanillaEnchant() {
        return false;
    }

    @Override
    public String getEnchant() {
        return "JELLY_LEGS";
    }

    @Override
    public EnumEnchantType getEnchantType() {
        return EnumEnchantType.OTHER;
    }

    @Override
    public <T extends Event> void runTask(T event) {
        EntityDamageEvent e = (EntityDamageEvent) event;

        if (!(e.getEntity() instanceof Player)) return;

        Player faller = (Player) e.getEntity();

        if (getEnchantInformation(faller)) e.setCancelled(true);

    }

    private boolean getEnchantInformation(Player player) {

        AtomicBoolean doesContain = new AtomicBoolean(false);

        Object2ObjectMap<EnchantSave, Integer> getEnchants = CustomEnchants.getInstance().getEnchantsFromPlayer(player);

        getEnchants.forEach((enchant, level) -> {
            if (!enchant.getName().equals(this.getName())) return;
            doesContain.set(true);
        });

        return doesContain.get();
    }

}
