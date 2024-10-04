package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.safari.config.saves;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class EquipmentSave {

    String material;
    String texture;
    String color;
    List<String> enchants = new ArrayList<>();

    public EquipmentSave(String material, String texture, String color, List<String> enchants) {
        this.material = material;
        this.texture = texture;
        this.color = color;
        this.enchants = enchants;
    }

}
