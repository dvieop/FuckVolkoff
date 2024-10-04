package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.garrison.config.saves;

import lombok.Getter;

import java.util.List;

@Getter
public class HologramSave {

    String headTexture;
    String location;
    List<String> lore;

    public HologramSave(String headTexture, String location, List<String> lore) {
        this.headTexture = headTexture;
        this.location = location;
        this.lore = lore;
    }

}
