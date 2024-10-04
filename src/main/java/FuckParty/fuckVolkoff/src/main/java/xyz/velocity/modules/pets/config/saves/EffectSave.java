package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.pets.config.saves;

import lombok.Getter;

@Getter
public class EffectSave {

    String name;
    String effectLore;

    public EffectSave(String name, String effectLore) {
        this.name = name;
        this.effectLore = effectLore;
    }

}
