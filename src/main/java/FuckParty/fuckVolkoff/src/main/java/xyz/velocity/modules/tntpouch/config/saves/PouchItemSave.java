package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.tntpouch.config.saves;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class PouchItemSave {

    String material;
    int data;
    int amount;
    String displayName;
    List<String> lore = new ArrayList<>();

    public PouchItemSave(String material, int data, int amount, String displayName) {
        this.material = material;
        this.data = data;
        this.displayName = displayName;
        this.amount = amount;
    }

}
