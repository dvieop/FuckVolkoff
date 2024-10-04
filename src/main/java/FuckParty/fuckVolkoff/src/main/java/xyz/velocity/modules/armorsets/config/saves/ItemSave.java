package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.armorsets.config.saves;

import lombok.Getter;

import java.io.Serializable;
import java.util.List;

@Getter
public class ItemSave implements Serializable {

    String name;
    String displayName;
    List<String> lore;
    List<String> enchants;
    String material;
    EffectsToggleSave customEffects;
    ArmorsetToggleSave armorsetBind;
    NBTSave nbt;

    public ItemSave(String name, String displayName, List<String> lore, List<String> enchants, String material, EffectsToggleSave effects, ArmorsetToggleSave armor, NBTSave nbt) {
        this.name = name;
        this.displayName = displayName;
        this.lore = lore;
        this.enchants = enchants;
        this.material = material;
        this.customEffects = effects;
        this.armorsetBind = armor;
        this.nbt = nbt;
    }

}
