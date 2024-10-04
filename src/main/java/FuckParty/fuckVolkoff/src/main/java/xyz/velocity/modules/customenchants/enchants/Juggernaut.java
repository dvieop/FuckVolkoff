package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.customenchants.enchants;

import com.google.gson.JsonObject;
import org.bukkit.event.Event;
import xyz.velocity.modules.customenchants.EnumEnchantType;
import xyz.velocity.modules.customenchants.config.CustomEnchantConfig;
import xyz.velocity.modules.customenchants.config.saves.EnchantSave;
import xyz.velocity.modules.customenchants.annotations.Enchant;

import java.util.Arrays;

@Enchant
public class Juggernaut extends AbstractEnchant {

    private final CustomEnchantConfig config;

    public Juggernaut() {
        this.config = CustomEnchantConfig.getInstance();

        EnchantSave enchant = new EnchantSave(true, "Juggernaut", "&cJuggernaut", Arrays.asList("&7Gives you a resistance effect based on level!"), "CHESTPLATE", "DAMAGE_RESISTANCE:<level>", 3, 2, 50, 1, false, new JsonObject());

        if (!config.getEnchantList().stream().anyMatch(obj -> obj.getName().equals(enchant.getName()))) {
            config.getEnchantList().add(enchant);
        }
    }

    @Override
    public boolean isEnabled() {
        return CustomEnchantConfig.getInstance().getEnchantList().stream().filter(obj -> obj.getName().equals(this.getName())).findFirst().get().isEnabled();
    }

    @Override
    public String getName() {
        return "Juggernaut";
    }

    @Override
    public boolean isVanillaEnchant() {
        return true;
    }

    @Override
    public String getEnchant() {
        return "DAMAGE_RESISTANCE";
    }

    @Override
    public EnumEnchantType getEnchantType() {
        return null;
    }

    @Override
    public <T extends Event> void runTask(T event) {

    }

}
