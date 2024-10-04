package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.stronghold.config.saves;

import lombok.Getter;

@Getter
public class WallLocations {

    String location1;
    String location2;

    public WallLocations(String location1, String location2) {
        this.location1 = location1;
        this.location2 = location2;
    }

}
