package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.armorsets.config.saves;

import lombok.Getter;

@Getter
public class NBTSave {

    boolean enabled;
    String compound;
    String key;
    String value;

    public NBTSave(boolean enabled, String compound, String key, String value) {
        this.enabled = enabled;
        this.compound = compound;
        this.key = key;
        this.value = value;
    }

}
