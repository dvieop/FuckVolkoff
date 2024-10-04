package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.mobarena.config.saves;

import lombok.Getter;
import xyz.velocity.modules.safari.config.saves.EquipmentSave;

import java.util.List;


@Getter
public class MonsterSave {

    String type;
    String displayName;
    int chance;
    int health;
    int baseDamage;
    List<EquipmentSave> equipment;

    public MonsterSave(String type, String displayName, int chance, int health, int baseDamage, List<EquipmentSave> equipment) {
        this.type = type;
        this.displayName = displayName;
        this.chance = chance;
        this.health = health;
        this.baseDamage = baseDamage;
        this.equipment = equipment;
    }

}
