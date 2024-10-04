package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.stronghold.config.saves;

import lombok.Getter;
import xyz.velocity.modules.safari.config.saves.EquipmentSave;

import java.util.List;

@Getter
public class MobSave {

    boolean enabled;
    String displayName;
    String mobType;
    int health;
    int damage;
    int maxMobs;
    int spawnInterval;
    List<String> spawnLocations;
    List<RewardSave> rewards;
    List<EquipmentSave> equipment;
    List<String> effects;

    public MobSave(boolean enabled, String displayName, String mobType, int health, int damage, int maxMobs, int spawnInterval, List<String> spawnLocations, List<RewardSave> rewards, List<EquipmentSave> equipment, List<String> effects) {
        this.enabled = enabled;
        this.displayName = displayName;
        this.mobType = mobType;
        this.health = health;
        this.damage = damage;
        this.maxMobs = maxMobs;
        this.spawnInterval = spawnInterval;
        this.spawnLocations = spawnLocations;
        this.rewards = rewards;
        this.equipment = equipment;
        this.effects = effects;
    }

}
