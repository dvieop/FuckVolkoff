package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.pets.config.saves;

import lombok.Getter;

import java.util.List;

@Getter
public class LevelEffectSave {

    int level;
    List<String> effects;

    public LevelEffectSave(int level, List<String> effects) {
        this.level = level;
        this.effects = effects;
    }

}
