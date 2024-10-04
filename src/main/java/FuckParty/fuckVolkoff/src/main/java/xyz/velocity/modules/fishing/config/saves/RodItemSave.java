package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.fishing.config.saves;

import lombok.Getter;

import java.util.List;

@Getter
public class RodItemSave {

    String name;
    String displayName;
    String material;
    List<String> lore;
    List<String> whitelistedWorlds;

    public RodItemSave(String name, String displayName, String material, List<String> lore, List<String> whitelistedWorlds) {
        this.name = name;
        this.displayName = displayName;
        this.material = material;
        this.lore = lore;
        this.whitelistedWorlds = whitelistedWorlds;
    }

}
