package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.mining;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import xyz.velocity.modules.mining.config.saves.TypeSave;

@Getter
public class OreCache {

    @Setter
    long cooldown;

    Location location;
    TypeSave typeSave;

    public OreCache(int cooldown, Location location, TypeSave typeSave) {
        this.cooldown = cooldown;
        this.location = location;
        this.typeSave = typeSave;
    }

    public boolean isOnCooldown() {
        return cooldown > System.currentTimeMillis();
    }

}
