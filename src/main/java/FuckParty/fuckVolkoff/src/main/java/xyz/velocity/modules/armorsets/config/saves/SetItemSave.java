package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.armorsets.config.saves;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class SetItemSave {

    String displayName;
    String material;
    String color;
    List<String> enchants = new ArrayList<>();

    public SetItemSave(String displayName, String material, String color, List<String> enchants) {
        this.displayName = displayName;
        this.material = material;
        this.color = color;
        this.enchants = enchants;
    }

}
