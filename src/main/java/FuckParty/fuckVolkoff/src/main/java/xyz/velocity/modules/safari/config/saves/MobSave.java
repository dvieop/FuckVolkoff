package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.safari.config.saves;

import lombok.Getter;

import java.util.List;

@Getter
public class MobSave {

    String type;
    String displayName;
    int chance;
    int health;
    int damage;
    int xp;
    List<EquipmentSave> equipment;
    List<String> effects;

    public MobSave(String type, String displayName, int chance, int health, int damage, int xp, List<EquipmentSave> equipment, List<String> effects) {
        this.type = type;
        this.displayName = displayName;
        this.chance = chance;
        this.health = health;
        this.damage = damage;
        this.equipment = equipment;
        this.effects = effects;
    }

}
