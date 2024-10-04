package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.pets.config.saves;

import lombok.Getter;

@Getter
public class XpSave {

    String effect;
    int cooldown;

    public XpSave(String effect, int cooldown) {
        this.effect = effect;
        this.cooldown = cooldown;
    }

}
