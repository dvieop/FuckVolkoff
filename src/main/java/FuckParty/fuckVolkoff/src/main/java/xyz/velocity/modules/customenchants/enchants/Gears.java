package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.customenchants.enchants;

import com.google.gson.JsonObject;
import org.bukkit.event.Event;
import xyz.velocity.modules.customenchants.EnumEnchantType;
import xyz.velocity.modules.customenchants.config.CustomEnchantConfig;
import xyz.velocity.modules.customenchants.config.saves.EnchantSave;
import xyz.velocity.modules.customenchants.annotations.Enchant;

import java.util.Arrays;

@Enchant
public class Gears extends AbstractEnchant {

    private final CustomEnchantConfig config;

    public Gears() {
        this.config = CustomEnchantConfig.getInstance();

        EnchantSave enchant = new EnchantSave(true, "Gears", "&cGears", Arrays.asList("&7Gain a permanent speed effect!"), "BOOTS", "SPEED:<level>", 3, 2, 50, 1, false, new JsonObject());

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
        return "Gears";
    }

    @Override
    public boolean isVanillaEnchant() {
        return true;
    }

    @Override
    public String getEnchant() {
        return "SPEED";
    }

    @Override
    public EnumEnchantType getEnchantType() {
        return null;
    }

    @Override
    public <T extends Event> void runTask(T event) {

    }

}
