package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.customenchants.enchants;

import com.google.gson.JsonObject;
import org.bukkit.event.Event;
import xyz.velocity.modules.customenchants.EnumEnchantType;
import xyz.velocity.modules.customenchants.config.CustomEnchantConfig;
import xyz.velocity.modules.customenchants.config.saves.EnchantSave;
import xyz.velocity.modules.customenchants.annotations.Enchant;

import java.util.Arrays;

@Enchant
public class Owl extends AbstractEnchant {

    private final CustomEnchantConfig config;

    public Owl() {
        this.config = CustomEnchantConfig.getInstance();

        EnchantSave enchant = new EnchantSave(true, "Owl", "&bOwl", Arrays.asList("&7Provides you the night vision effect!"), "HELMET", "NIGHT_VISION:<level>", 1, 1, 50, 1, false, new JsonObject());

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
        return "Owl";
    }

    @Override
    public boolean isVanillaEnchant() {
        return true;
    }

    @Override
    public String getEnchant() {
        return "NIGHT_VISION";
    }

    @Override
    public EnumEnchantType getEnchantType() {
        return null;
    }

    @Override
    public <T extends Event> void runTask(T event) {

    }

}
