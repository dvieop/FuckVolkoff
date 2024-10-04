package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.mobarena.config.saves;

import lombok.Getter;
import xyz.velocity.modules.safari.config.saves.EquipmentSave;

import java.util.List;

@Getter
public class ClassSave {

    String name;
    String displayName;
    List<EquipmentSave> equipment;
    List<ItemSave> items;

    public ClassSave(String name, String displayName, List<EquipmentSave> equipment, List<ItemSave> items) {
        this.name = name;
        this.displayName = displayName;
        this.equipment = equipment;
        this.items = items;
    }

}
