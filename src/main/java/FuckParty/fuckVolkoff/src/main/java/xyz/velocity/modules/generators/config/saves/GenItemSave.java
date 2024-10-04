package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.generators.config.saves;

import lombok.Getter;

import java.util.List;

@Getter
public class GenItemSave {

    String skullTexture;
    String name;
    List<String> lore;

    public GenItemSave(String skullTexture, String name, List<String> lore) {
        this.skullTexture = skullTexture;
        this.name = name;
        this.lore = lore;
    }

}
