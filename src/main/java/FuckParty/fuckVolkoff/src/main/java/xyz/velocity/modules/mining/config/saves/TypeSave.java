package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.mining.config.saves;

import lombok.Getter;

import java.util.List;

@Getter
public class TypeSave {

    String typeName;
    String chatName;
    String material;
    int cooldown;
    List<String> dropIds;

    public TypeSave(String typeName, String chatName, String material, int cooldown, List<String> dropIds) {
        this.typeName = typeName;
        this.chatName = chatName;
        this.material = material;
        this.cooldown = cooldown;
        this.dropIds = dropIds;
    }

}
