package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.stronghold.config.saves;

import lombok.Getter;

import java.io.Serializable;
import java.util.List;

@Getter
public class WallRegionSave implements Serializable {

    boolean enabled;
    int uses;
    String material;
    int regenInterval;
    List<WallLocations> regions;

    public WallRegionSave(boolean enabled, int uses, String material, int regenInterval, List<WallLocations> regions) {
        this.enabled = enabled;
        this.uses = uses;
        this.material = material;
        this.regenInterval = regenInterval;
        this.regions = regions;
    }

}
