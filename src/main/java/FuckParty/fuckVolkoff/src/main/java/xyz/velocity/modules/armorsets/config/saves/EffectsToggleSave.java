package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.armorsets.config.saves;

import lombok.Getter;

import java.util.List;

@Getter
public class EffectsToggleSave {

    boolean enabled;
    List<String> effects;

    public EffectsToggleSave(boolean enabled, List<String> effects) {
        this.enabled = enabled;
        this.effects = effects;
    }

}
