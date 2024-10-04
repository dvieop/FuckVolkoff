package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.pets.config.saves;

import lombok.Getter;

import java.util.List;

@Getter
public class AttachableItemSave {

    String name;
    String material;
    String displayName;
    List<String> lore;
    String attachedLore;
    int data;

    public AttachableItemSave(String name, String material, String displayName, List<String> lore, String attachedLore, int data) {
        this.name = name;
        this.material = material;
        this.displayName = displayName;
        this.lore = lore;
        this.attachedLore = attachedLore;
        this.data = data;
    }

}
