package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.customenchants.config.saves;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Item {

    String displayName;
    List<String> lore;
    String item;
    List<String> enchants = new ArrayList<>();
    List<KitEnchant> customEnchants = new ArrayList<>();

    public Item(String displayName, List<String> lore, String item, List<String> enchants, List<KitEnchant> customEnchants) {
        this.displayName = displayName;
        this.lore = lore;
        this.item = item;
        this.enchants = enchants;
        this.customEnchants = customEnchants;
    }

}
