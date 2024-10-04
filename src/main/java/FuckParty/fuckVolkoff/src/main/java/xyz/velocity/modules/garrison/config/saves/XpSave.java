package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.garrison.config.saves;

import lombok.Getter;

@Getter
public class XpSave {

    String name;
    int xp;

    public XpSave(String name, int xp) {
        this.name = name;
        this.xp = xp;
    }

}
