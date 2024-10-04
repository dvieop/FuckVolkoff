package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.customenchants.config.saves;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.Getter;

import java.io.Serializable;
import java.util.List;

@Getter
public class EnchantSave implements Serializable {

    boolean enabled;
    String name;
    String displayName;
    List<String> lore;
    String applicableTo;
    String enchant;
    int enchantTier;
    int maxLevel;
    double chance;
    int cooldown;
    boolean procAlert;
    JsonArray extra = new JsonArray();

    public EnchantSave(boolean enabled, String enchantName, String displayName, List<String> lore, String applicableTo, String enchant, int enchantTier, int maxLevel, int enchantChance, int cooldown, boolean procAlert, JsonObject obj) {
        this.enabled = enabled;
        this.name = enchantName;
        this.displayName = displayName;
        this.lore = lore;
        this.applicableTo = applicableTo;
        this.enchant = enchant;
        this.enchantTier = enchantTier;
        this.maxLevel = maxLevel;
        this.chance = enchantChance;
        this.cooldown = cooldown;
        this.procAlert = procAlert;
        this.extra.add(obj);
    }

}
