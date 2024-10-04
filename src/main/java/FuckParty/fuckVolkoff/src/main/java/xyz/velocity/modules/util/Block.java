package FuckParty.fuckVolkoff.src.main.java.xyz.velocity.modules.util;

import org.bukkit.Location;

public class Block {

    Location location;
    int uses;

    public Block(Location location, int uses) {
        this.location = location;
        this.uses = uses;
    }

    public Location getLocation() {
        return location;
    }

    public int getUses() {
        return uses;
    }

    public void setUses(int uses) {
        this.uses = uses;
    }

}
