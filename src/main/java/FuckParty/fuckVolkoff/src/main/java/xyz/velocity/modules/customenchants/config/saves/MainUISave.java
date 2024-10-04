package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.customenchants.config.saves;

import lombok.Getter;

import java.io.Serializable;

@Getter
public class MainUISave implements Serializable {

    String name;
    String displayName;
    String displayItem;
    int slot;
    String lore;

    public MainUISave(String name, String displayName, String displayItem, int slot, String lore) {
        this.name = name;
        this.displayName = displayName;
        this.displayItem = displayItem;
        this.slot = slot;
        this.lore = lore;
    }

}
