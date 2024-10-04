package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.masks.config;

import lombok.Getter;

import java.util.List;

@Getter
public class MaskSave {

    int tier;
    String name;
    String displayName;
    List<String> lore;
    String material;
    String texture;
    List<String> vanillaEffects;
    List<String> customEffects;
    MultiMaskSave multiMask;
    int chanceToObtain;

    public MaskSave(int tier, String name, String displayName, List<String> lore, String material, String texture, List<String> vanillaEffects, List<String> customEffects, MultiMaskSave multiMaskSave, int chanceToObtain) {
        this.tier = tier;
        this.name = name;
        this.displayName = displayName;
        this.lore = lore;
        this.material = material;
        this.texture = texture;
        this.vanillaEffects = vanillaEffects;
        this.customEffects = customEffects;
        this.multiMask = multiMaskSave;
        this.chanceToObtain = chanceToObtain;
    }

}
