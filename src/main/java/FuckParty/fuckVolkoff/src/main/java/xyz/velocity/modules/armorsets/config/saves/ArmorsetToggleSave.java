package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.armorsets.config.saves;

import lombok.Getter;

@Getter
public class ArmorsetToggleSave {

    boolean enabled;
    String armor;

    public ArmorsetToggleSave(boolean enabled, String armor) {
        this.enabled = enabled;
        this.armor = armor;
    }

}
