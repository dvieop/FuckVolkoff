package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.customitems.config.saves;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.Getter;

import java.io.Serializable;
import java.util.List;

@Getter
public class ItemSave implements Serializable {

    boolean enabled;
    String name;
    String displayName;
    String displayItem;
    int damage;
    List<String> lore;
    List<String> effects;
    int cooldown;
    JsonArray extra = new JsonArray();

    public ItemSave(boolean enabled, String name, String displayName, String displayItem, int damage, List<String> lore, List<String> effects, int cooldown, JsonObject obj) {
        this.enabled = enabled;
        this.name = name;
        this.displayName = displayName;
        this.displayItem = displayItem;
        this.damage = damage;
        this.lore = lore;
        this.effects = effects;
        this.cooldown = cooldown;
        this.extra.add(obj);
    }

}
