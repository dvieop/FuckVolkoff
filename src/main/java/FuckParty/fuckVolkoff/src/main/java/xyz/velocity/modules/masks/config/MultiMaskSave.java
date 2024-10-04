package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.masks.config;

import lombok.Getter;

@Getter
public class MultiMaskSave {

    boolean enabled;
    int max;
    String lore;

    public MultiMaskSave(boolean enabled, int max, String lore) {
        this.enabled = enabled;
        this.max = max;
        this.lore = lore;
    }
}
