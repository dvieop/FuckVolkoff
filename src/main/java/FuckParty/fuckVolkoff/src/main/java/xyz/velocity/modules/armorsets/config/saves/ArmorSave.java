package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.armorsets.config.saves;

import lombok.Getter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
public class ArmorSave implements Serializable {

    String name;
    String chatName;
    List<String> whitelistedWorlds = new ArrayList<>();
    List<String> armorLore = new ArrayList<>();
    List<String> vanillaEffects = new ArrayList<>();
    List<String> customEffects = new ArrayList<>();
    List<SetItemSave> items = new ArrayList<>();
    int chanceToObtain;

    public ArmorSave(String name, String chatName, List<String> whitelistedWorlds, List<String> armorLore, List<String> vanillaEffects, List<String> customEffects, List<SetItemSave> items, int chanceToObtain) {
        this.name = name;
        this.chatName = chatName;
        this.whitelistedWorlds = whitelistedWorlds;
        this.armorLore = armorLore;
        this.vanillaEffects = vanillaEffects;
        this.customEffects = customEffects;
        this.items = items;
        this.chanceToObtain = chanceToObtain;
    }

}
