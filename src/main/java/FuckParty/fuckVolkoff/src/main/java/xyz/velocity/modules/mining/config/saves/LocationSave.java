package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.mining.config.saves;

import lombok.Getter;

@Getter
public class LocationSave {

    String pos1;
    String pos2;

    public LocationSave(String pos1, String pos2) {
        this.pos1 = pos1;
        this.pos2 = pos2;
    }

}
