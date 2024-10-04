package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.fishing.config.saves;

import lombok.Getter;

@Getter
public class FishingInventorySave {

    String guiName;
    int size;

    public FishingInventorySave(String guiName, int size) {
        this.guiName = guiName;
        this.size = size;
    }

}
