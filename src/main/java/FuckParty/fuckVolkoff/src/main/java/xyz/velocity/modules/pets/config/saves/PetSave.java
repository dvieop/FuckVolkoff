package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.pets.config.saves;

import lombok.Getter;

import java.util.List;

@Getter
public class PetSave {

    String name;
    int tier;
    double chance;
    String texture;
    String type;
    String displayName;
    List<String> itemLore;
    List<String> inventoryLore;
    List<LevelEffectSave> levelEffects;

    public PetSave(String name, int tier, double chance, String displayName, String texture, String type, List<String> itemLore, List<String> inventoryLore, List<LevelEffectSave> levelEffects) {
        this.name = name;
        this.tier = tier;
        this.chance = chance;
        this.displayName = displayName;
        this.texture = texture;
        this.type = type;
        this.itemLore = itemLore;
        this.inventoryLore = inventoryLore;
        this.levelEffects = levelEffects;
    }

}
