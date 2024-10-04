package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.customenchants.config.saves;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.Getter;

import java.util.List;

@Getter
public class EnchantItemSave {

    boolean enabled;
    String name;
    String displayName;
    List<String> lore;
    List<String> appliedLore;
    String material;
    int damage;
    boolean glow;
    JsonArray extra = new JsonArray();

    public EnchantItemSave(boolean enabled, String name, String displayName, List<String> lore, List<String> appliedLore, String material, int damage, boolean glow, JsonObject obj) {
        this.enabled = enabled;
        this.name = name;
        this.displayName = displayName;
        this.lore = lore;
        this.appliedLore = appliedLore;
        this.material = material;
        this.damage = damage;
        this.glow = glow;
        this.extra.add(obj);
    }

}
